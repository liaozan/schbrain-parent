package com.schbrain.framework.autoconfigure.apollo.util;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.enums.ConfigSourceType;
import com.schbrain.common.util.ConfigurationPropertiesUtils;
import com.schbrain.common.util.properties.SchbrainMapPropertySource;
import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.properties.ApolloProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APP_ID;

/**
 * @author liaozan
 * @since 2021/12/6
 */
public class ConfigUtils {

    private static final Log LOGGER = LogFactory.getLog(ConfigUtils.class);

    private static final boolean APOLLO_DISABLED = System.getProperty(APP_ID) == null;

    public static boolean isApolloDisabled() {
        return APOLLO_DISABLED;
    }

    public static <T extends ConfigurableProperties> T loadConfig(ConfigurableEnvironment environment, Class<T> propertyClass) {
        // load default and local properties
        T target = BeanUtils.instantiateClass(propertyClass).bindOrCreate(environment, false);
        Map<String, Object> defaultProperties = ConfigurationPropertiesUtils.toMap(target);
        // load remote config
        Map<String, Object> loadedProperties = loadConfig(target.getNamespace(), target.getPrefix());
        // merge
        ApolloProperties apolloProperties = ApolloProperties.get(environment);
        Map<String, Object> mergedProperties = mergeProperties(loadedProperties, defaultProperties, apolloProperties.isRemoteFirst());
        // add to environment
        addToEnvironment(environment, target.getName(), mergedProperties);
        // rebind after addToEnvironment
        return target.bindOrCreate(environment, true);
    }

    public static Map<String, Object> loadConfig(String namespace) {
        return loadConfig(namespace, null);
    }

    public static Map<String, Object> loadConfig(String namespace, String prefix) {
        if (isApolloDisabled()) {
            return new LinkedHashMap<>();
        }
        Config config = ConfigService.getConfig(namespace);
        if (config.getSourceType() == ConfigSourceType.LOCAL) {
            LOGGER.warn(String.format("Failed to get config from Apollo namespace: %s, will use the local cache value", namespace));
        }

        Map<String, Object> configs = new LinkedHashMap<>();
        for (String propertyName : config.getPropertyNames()) {
            if (prefix != null) {
                if (!propertyName.startsWith(prefix)) {
                    continue;
                }
            }
            String propertyValue = config.getProperty(propertyName, null);
            configs.put(propertyName, propertyValue);
        }
        return configs;
    }

    public static <T extends ConfigurableProperties> void addToEnvironment(ConfigurableEnvironment environment, T properties) {
        addToEnvironment(environment, new SchbrainMapPropertySource(properties.getName(), properties));
    }

    public static void addToEnvironment(ConfigurableEnvironment environment, String name, Map<String, Object> properties) {
        addToEnvironment(environment, new SchbrainMapPropertySource(name, properties));
    }

    public static void addToEnvironment(ConfigurableEnvironment environment, MapPropertySource propertySource) {
        MutablePropertySources propertySources = environment.getPropertySources();
        String propertySourceName = propertySource.getName();

        boolean alreadyExist = propertySources.contains(propertySourceName);
        if (alreadyExist) {
            PropertySource<?> existing = propertySources.get(propertySourceName);
            if (existing instanceof MapPropertySource) {
                Map<String, Object> existingSource = ((MapPropertySource) existing).getSource();
                existingSource.putAll(propertySource.getSource());
            } else {
                LOGGER.warn("Existing propertySource is not an instance of MapPropertySource, overwrite existing...");
                propertySources.replace(propertySourceName, propertySource);
            }
        } else {
            propertySources.addLast(propertySource);
        }
        resolvePlaceHolders(environment, propertySource);
        ConfigurationPropertySources.attach(environment);
        DefaultPropertiesPropertySource.moveToEnd(environment);
    }

    public static void resolvePlaceHolders(ConfigurableEnvironment environment, MapPropertySource propertySource) {
        Map<String, Object> source = propertySource.getSource();
        for (Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String resolvedValue = environment.resolvePlaceholders((String) value);
                source.put(entry.getKey(), resolvedValue);
            }
        }
    }

    private static Map<String, Object> mergeProperties(Map<String, Object> loadedProperties,
                                                       Map<String, Object> defaultProperties,
                                                       boolean remoteFirst) {
        Map<String, Object> mergedProperties;
        if (remoteFirst) {
            defaultProperties.putAll(loadedProperties);
            mergedProperties = defaultProperties;
        } else {
            loadedProperties.putAll(defaultProperties);
            mergedProperties = loadedProperties;
        }
        return mergedProperties;
    }

}