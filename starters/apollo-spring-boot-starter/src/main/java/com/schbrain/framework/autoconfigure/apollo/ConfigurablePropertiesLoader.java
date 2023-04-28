package com.schbrain.framework.autoconfigure.apollo;

import cn.hutool.core.thread.GlobalThreadPool;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.schbrain.common.util.properties.SchbrainMapPropertySource;
import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.listener.PropertiesPreparedEvent;
import com.schbrain.framework.autoconfigure.apollo.listener.PropertiesPreparedEventListener;
import com.schbrain.framework.autoconfigure.apollo.properties.ApolloProperties;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

import java.util.List;

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

    ConfigurablePropertiesLoader(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(ConfigurablePropertiesLoader.class);
    }

    void load(ConfigurableEnvironment environment, SpringApplication application) {
        List<ConfigurableProperties> configurableProperties = loadFactories(ConfigurableProperties.class, getClass().getClassLoader());
        if (CollectionUtils.isEmpty(configurableProperties)) {
            log.warn("There is no configuration properties found");
            return;
        }

        ApplicationEventMulticaster eventMulticaster = createEventMulticaster(application);

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
            SchbrainMapPropertySource propertySource = ConfigUtils.toPropertySource(namespace, config);
            if (propertySource == null) {
                log.warn("No configuration properties loaded under namespace: " + namespace);
                return;
            }
            ConfigUtils.resolvePlaceHolders(environment, propertySource);

            // early add to environment to support properties bind
            compositePropertySource.addPropertySource(propertySource);

            ConfigurableProperties boundProperties = properties.bind(environment);
            eventMulticaster.multicastEvent(new PropertiesPreparedEvent(boundProperties, propertySource, environment, application));
        });
    }

    private ApplicationEventMulticaster createEventMulticaster(SpringApplication application) {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(GlobalThreadPool.getExecutor());
        for (ApplicationListener<?> listener : application.getListeners()) {
            if (ClassUtils.isAssignableValue(PropertiesPreparedEventListener.class, listener)) {
                eventMulticaster.addApplicationListener(listener);
            }
        }
        return eventMulticaster;
    }

}