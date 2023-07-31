package com.schbrain.framework.autoconfigure.cache.provider.redis;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-27
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties implements ConfigurableProperties {

    @Override
    public String getNamespace() {
        return "redis-common";
    }

}
