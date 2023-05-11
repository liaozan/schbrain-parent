package com.schbrain.framework.autoconfigure.cache.provider.redis;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties extends ConfigurableProperties {

    @Override
    public String getNamespace() {
        return "redis-common";
    }

}