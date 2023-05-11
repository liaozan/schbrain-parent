package com.schbrain.framework.autoconfigure.cache.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhuyf
 * @since 2022/7/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "schbrain.cache")
public class CacheProperties extends ConfigurableProperties {

    /**
     * cache prefix
     */
    private String prefix;

    /**
     * cache prefix delimiter
     */
    private String delimiter = ":";

    /**
     * whatever to enable prefix append
     */
    private boolean appendPrefix = true;

    @Override
    public String getNamespace() {
        return "cache-common";
    }

}