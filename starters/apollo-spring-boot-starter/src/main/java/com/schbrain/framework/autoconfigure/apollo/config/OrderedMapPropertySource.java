package com.schbrain.framework.autoconfigure.apollo.config;

import org.springframework.core.env.MapPropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ordered property source
 *
 * @author liaozan
 * @since 2021/12/6
 */
public class OrderedMapPropertySource extends MapPropertySource {

    public OrderedMapPropertySource(String name, Map<String, Object> source) {
        super(name, new LinkedHashMap<>(source));
    }

    public void addProperties(Map<String, Object> properties) {
        getSource().putAll(properties);
    }

    public void addProperty(String propertyName, Object propertyValue) {
        getSource().put(propertyName, propertyValue);
    }

}
