package com.schbrain.common.util.properties;

import org.springframework.core.env.MapPropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * mark class to ensure property order
 *
 * @author liaozan
 * @since 2021/12/6
 */
public class SchbrainMapPropertySource extends MapPropertySource {

    public SchbrainMapPropertySource(String name, Map<String, String> source) {
        super(name, new LinkedHashMap<>(source));
    }

    public void addProperties(Map<String, String> properties) {
        getSource().putAll(properties);
    }

}