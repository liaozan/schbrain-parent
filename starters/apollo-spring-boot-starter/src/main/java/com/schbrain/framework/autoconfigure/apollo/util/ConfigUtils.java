package com.schbrain.framework.autoconfigure.apollo.util;

import com.ctrip.framework.apollo.Config;
import com.google.common.collect.Maps;
import com.schbrain.framework.autoconfigure.apollo.config.OrderedMapPropertySource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author liaozan
 * @since 2021/12/6
 */
@Slf4j
public class ConfigUtils {

    @Nullable
    public static OrderedMapPropertySource toPropertySource(String name, Config config) {
        Set<String> propertyNames = config.getPropertyNames();
        if (propertyNames.isEmpty()) {
            return null;
        }
        Map<String, String> configs = Maps.newLinkedHashMapWithExpectedSize(propertyNames.size());
        for (String propertyName : propertyNames) {
            String property = config.getProperty(propertyName, null);
            configs.put(propertyName, property);
        }
        return new OrderedMapPropertySource(name, configs);
    }

    public static void resolvePlaceHolders(ConfigurableEnvironment environment, OrderedMapPropertySource propertySource) {
        Map<String, Object> source = propertySource.getSource();
        for (Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String resolvedValue = environment.resolvePlaceholders((String) value);
                source.put(entry.getKey(), resolvedValue);
            }
        }
    }

}