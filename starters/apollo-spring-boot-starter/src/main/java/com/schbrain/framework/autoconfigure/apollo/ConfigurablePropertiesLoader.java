package com.schbrain.framework.autoconfigure.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.schbrain.common.util.properties.OrderedMapPropertySource;
import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.event.ConcurrentEventMulticaster;
import com.schbrain.framework.autoconfigure.apollo.event.PropertiesPreparedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.PropertiesPreparedEventListener;
import com.schbrain.framework.autoconfigure.apollo.properties.ApolloProperties;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Set;

import static org.springframework.core.io.support.SpringFactoriesLoader.loadFactories;

/**
 * @author liaozan
 * @since 2023-04-29
 */
class ConfigurablePropertiesLoader {

    /**
     * the name of properties propertySource
     */
    private static final String PROPERTIES_PROPERTY_SOURCE = "ConfigurablePropertiesPropertySource";

    private final Log log;

    private final DeferredLogFactory deferredLogFactory;

    private final ConfigurableEnvironment environment;

    private final SpringApplication application;

    ConfigurablePropertiesLoader(DeferredLogFactory deferredLogFactory, ConfigurableEnvironment environment, SpringApplication application) {
        this.log = deferredLogFactory.getLog(ConfigurablePropertiesLoader.class);
        this.deferredLogFactory = deferredLogFactory;
        this.environment = environment;
        this.application = application;
    }

    void load() {
        List<ConfigurableProperties> configurableProperties = loadFactories(ConfigurableProperties.class, getClass().getClassLoader());
        if (CollectionUtils.isEmpty(configurableProperties)) {
            log.warn("There is no configuration properties found");
            return;
        }

        ConcurrentEventMulticaster eventMulticaster = createEventMulticaster(application.getListeners());

        ApolloProperties apolloProperties = ApolloProperties.get(environment);

        // MUST NOT use CachedCompositePropertySource
        CompositePropertySource compositePropertySource = new CompositePropertySource(PROPERTIES_PROPERTY_SOURCE);
        if (apolloProperties.isRemoteFirst()) {
            environment.getPropertySources().addFirst(compositePropertySource);
        } else {
            environment.getPropertySources().addLast(compositePropertySource);
        }

        configurableProperties.forEach(properties -> {
            String namespace = properties.getDefaultNamespace();
            Config config = ConfigService.getConfig(namespace);
            OrderedMapPropertySource propertySource = ConfigUtils.toPropertySource(namespace, config);
            if (propertySource == null) {
                log.warn("No configuration properties loaded under namespace: " + namespace);
                return;
            }
            // early add to environment to support properties bind
            compositePropertySource.addPropertySource(propertySource);
            // resolve any placeHolders
            ConfigUtils.resolvePlaceHolders(environment, propertySource);
            // multicast event
            eventMulticaster.multicastEvent(createEvent(propertySource, properties));
        });
    }

    private PropertiesPreparedEvent createEvent(OrderedMapPropertySource propertySource, ConfigurableProperties properties) {
        ConfigurableProperties boundProperties = properties.bind(environment);
        return new PropertiesPreparedEvent(environment, deferredLogFactory, propertySource, boundProperties, application);
    }

    private ConcurrentEventMulticaster createEventMulticaster(Set<ApplicationListener<?>> listeners) {
        ConcurrentEventMulticaster eventMulticaster = new ConcurrentEventMulticaster();
        for (ApplicationListener<?> listener : listeners) {
            if (ClassUtils.isAssignableValue(PropertiesPreparedEventListener.class, listener)) {
                eventMulticaster.addApplicationListener(listener);
            }
        }
        return eventMulticaster;
    }

}