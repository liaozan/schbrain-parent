package com.schbrain.framework.autoconfigure.cache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhuyf
 * @since 2022/7/26
 */
@Data
@ConfigurationProperties(prefix = "schbrain.cache")
public class CacheProperties {

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

}