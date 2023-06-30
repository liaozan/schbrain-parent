package com.schbrain.framework.autoconfigure.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.schbrain.common.util.properties.OrderedMapPropertySource;
import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.ConfigLoadedEventListener;
import com.schbrain.framework.autoconfigure.apollo.properties.ApolloProperties;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
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

    private final Log log;

    private final DeferredLogFactory deferredLogFactory;

    ConfigurablePropertiesLoader(DeferredLogFactory deferredLogFactory) {
        this.deferredLogFactory = deferredLogFactory;
        this.log = deferredLogFactory.getLog(ConfigurablePropertiesLoader.class);
    }

    void load(ConfigurableEnvironment environment, SpringApplication application) {
        List<ConfigurableProperties> configurableProperties = loadFactories(ConfigurableProperties.class, getClass().getClassLoader());
        if (CollectionUtils.isEmpty(configurableProperties)) {
            log.warn("There is no configuration properties found");
            return;
        }

        ApplicationEventMulticaster eventMulticaster = createEventMulticaster(application.getListeners());
        boolean remoteFirst = ApolloProperties.get(environment).isRemoteFirst();

        configurableProperties.parallelStream().forEach(properties -> {
            OrderedMapPropertySource propertySource = loadFromRemote(environment, remoteFirst, properties.getNamespace());
            if (propertySource == null) {
                return;
            }
            // multicast event
            ConfigLoadedEvent event = createEvent(environment, application, propertySource, properties);
            eventMulticaster.multicastEvent(event, ResolvableType.forClass(event.getClass()));
        });
    }

    private OrderedMapPropertySource loadFromRemote(ConfigurableEnvironment environment, boolean remoteFirst, String namespace) {
        Config config = ConfigService.getConfig(namespace);
        OrderedMapPropertySource propertySource = ConfigUtils.toPropertySource(namespace, config);
        if (propertySource == null) {
            log.warn("No configuration properties loaded under namespace: " + namespace);
            return null;
        }
        if (remoteFirst) {
            environment.getPropertySources().addFirst(propertySource);
        } else {
            environment.getPropertySources().addLast(propertySource);
        }
        // resolve any placeHolders
        ConfigUtils.resolvePlaceHolders(environment, propertySource);
        return propertySource;
    }

    private ConfigLoadedEvent createEvent(ConfigurableEnvironment environment, SpringApplication application,
                                          OrderedMapPropertySource propertySource, ConfigurableProperties properties) {
        ConfigurableProperties boundProperties = properties.bind(environment);
        return new ConfigLoadedEvent(environment, deferredLogFactory, propertySource, boundProperties, application);
    }

    private ApplicationEventMulticaster createEventMulticaster(Set<ApplicationListener<?>> listeners) {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        for (ApplicationListener<?> listener : listeners) {
            if (ClassUtils.isAssignableValue(ConfigLoadedEventListener.class, listener)) {
                eventMulticaster.addApplicationListener(listener);
            }
        }
        return eventMulticaster;
    }

}