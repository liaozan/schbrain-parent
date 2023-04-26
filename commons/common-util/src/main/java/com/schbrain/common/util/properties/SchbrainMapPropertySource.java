package com.schbrain.common.util.properties;

import com.schbrain.common.util.ConfigurationPropertiesUtils;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * mark class to ensure property order
 *
 * @author liaozan
 * @since 2021/12/6
 */
public class SchbrainMapPropertySource extends MapPropertySource {

    public SchbrainMapPropertySource(String name, Object source) {
        this(name, ConfigurationPropertiesUtils.toMap(source));
    }

    public SchbrainMapPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

}