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

    public OrderedMapPropertySource(String name, Map<String, String> source) {
        super(name, new LinkedHashMap<>(source));
    }

    public void addProperties(Map<String, String> properties) {
        getSource().putAll(properties);
    }

    public void addProperty(String propertyName, String propertyValue) {
        getSource().put(propertyName, propertyValue);
    }

}