package com.schbrain.framework.autoconfigure.cache;

import com.schbrain.framework.autoconfigure.cache.properties.CacheProperties;
import com.schbrain.framework.autoconfigure.cache.provider.CacheOperation;
import com.schbrain.framework.autoconfigure.cache.provider.PrefixedCacheOperation;
import com.schbrain.framework.autoconfigure.cache.provider.redis.RedisCacheConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * AutoConfiguration for schbrain cache
 *
 * @author zhuyf
 * @since 2022/7/25
 */
@Import(RedisCacheConfiguration.class)
@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnBean(CacheOperation.class)
    public PrefixedCacheOperation prefixedCacheOperation(CacheOperation cacheOperation, CacheProperties cacheProperties) {
        PrefixedCacheOperation operation = new PrefixedCacheOperation(cacheProperties, cacheOperation);
        CacheUtils.setCacheOperation(operation);
        return operation;
    }

}
