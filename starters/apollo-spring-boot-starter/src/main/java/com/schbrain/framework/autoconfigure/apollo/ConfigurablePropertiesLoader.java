package com.schbrain.framework.autoconfigure.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.google.common.collect.Maps;
import com.schbrain.common.util.ConfigurationPropertiesUtils;
import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.config.OrderedMapPropertySource;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.ConfigLoadedEventListener;
import com.schbrain.framework.autoconfigure.apollo.properties.ApolloProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.logging.Log;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import java.util.*;
import java.util.Map.Entry;

import static org.springframework.core.io.support.SpringFactoriesLoader.loadFactories;

/**
 * @author liaozan
 * @since 2023-04-29
 */
class ConfigurablePropertiesLoader {

    private final Log log;
    private final DeferredLogFactory deferredLogFactory;
    private final ConfigurableBootstrapContext bootstrapContext;

    ConfigurablePropertiesLoader(DeferredLogFactory deferredLogFactory, ConfigurableBootstrapContext bootstrapContext) {
        this.deferredLogFactory = deferredLogFactory;
        this.log = deferredLogFactory.getLog(ConfigurablePropertiesLoader.class);
        this.bootstrapContext = bootstrapContext;
    }

    /**
     * Do not use asynchronous or thread pool to speed up configuration loading, as this may break the priority of configuration properties
     */
    void load(ConfigurableEnvironment environment, SpringApplication application) {
        List<ConfigurableProperties> propertiesList = loadFactories(ConfigurableProperties.class, getClass().getClassLoader());
        if (CollectionUtils.isEmpty(propertiesList)) {
            log.warn("There is no configurable properties found");
            return;
        }

        ApplicationEventMulticaster eventMulticaster = createEventMulticaster(application.getListeners());
        boolean remoteFirst = ApolloProperties.get(environment).isRemoteFirst();

        for (ConfigurableProperties properties : propertiesList) {
            OrderedMapPropertySource propertySource = buildPropertySource(environment, remoteFirst, properties);
            ConfigLoadedEvent event = createEvent(environment, application, propertySource, properties);
            eventMulticaster.multicastEvent(event, ResolvableType.forClass(event.getClass()));
        }
    }

    private OrderedMapPropertySource buildPropertySource(ConfigurableEnvironment environment, boolean remoteFirst, ConfigurableProperties properties) {
        Map<String, Object> mergedProperties = loadAndMergeLocalDefaults(properties);
        OrderedMapPropertySource propertySource = createPropertySource(environment, properties.getNamespace(), mergedProperties);
        if (remoteFirst) {
            environment.getPropertySources().addFirst(propertySource);
        } else {
            environment.getPropertySources().addLast(propertySource);
        }
        return propertySource;
    }

    private Map<String, Object> loadAndMergeLocalDefaults(ConfigurableProperties properties) {
        String namespace = properties.getNamespace();
        Config config = ConfigService.getConfig(namespace);
        Map<String, Object> loadedProperties = toPropertiesMap(config);
        if (MapUtils.isEmpty(loadedProperties)) {
            log.warn("No configuration properties loaded under namespace: " + namespace);
        }
        Map<String, Object> defaultProperties = ConfigurationPropertiesUtils.toMap(properties);
        return mergeProperties(loadedProperties, defaultProperties);
    }

    private ConfigLoadedEvent createEvent(ConfigurableEnvironment environment, SpringApplication application,
                                          OrderedMapPropertySource propertySource, ConfigurableProperties properties) {
        ConfigurableProperties boundProperties = properties.bind(environment);
        return new ConfigLoadedEvent(environment, deferredLogFactory, propertySource, boundProperties, application, bootstrapContext);
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

    private Map<String, Object> toPropertiesMap(Config config) {
        Set<String> propertyNames = config.getPropertyNames();
        if (propertyNames.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> configs = Maps.newLinkedHashMapWithExpectedSize(propertyNames.size());
        for (String propertyName : propertyNames) {
            String property = config.getProperty(propertyName, null);
            configs.put(propertyName, property);
        }
        return configs;
    }

    private Map<String, Object> mergeProperties(Map<String, Object> loadedProperties, Map<String, Object> defaultProperties) {
        Map<String, Object> mergedProperties = new LinkedHashMap<>();
        mergedProperties.putAll(defaultProperties);
        mergedProperties.putAll(loadedProperties);
        return mergedProperties;
    }

    private OrderedMapPropertySource createPropertySource(Environment environment, String namespace, Map<String, Object> source) {
        for (Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String resolvedValue = environment.resolvePlaceholders((String) value);
                source.put(entry.getKey(), resolvedValue);
            }
        }
        return new OrderedMapPropertySource(namespace, source);
    }

}
