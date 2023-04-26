package com.schbrain.framework.autoconfigure.cache;

import com.schbrain.framework.autoconfigure.cache.properties.CacheProperties;
import com.schbrain.framework.autoconfigure.cache.provider.CacheProvider;
import com.schbrain.framework.autoconfigure.cache.provider.CacheProviderDelegate;
import com.schbrain.framework.autoconfigure.cache.provider.redis.SchbrainRedisCacheConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * AutoConfiguration for schbrain cache
 *
 * @author zhuyf
 * @since 2022/7/25
 */
@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties(CacheProperties.class)
@Import(SchbrainRedisCacheConfiguration.class)
public class SchbrainCacheAutoConfiguration {

    @Bean
    @ConditionalOnBean(CacheProvider.class)
    public CacheProviderDelegate cacheServiceDelegate(CacheProvider cacheProvider, CacheProperties cacheProperties,
                                                      Environment environment) {
        CacheProviderDelegate delegate = new CacheProviderDelegate(cacheProperties, cacheProvider, environment);
        CacheUtils.setCacheProvider(delegate);
        return delegate;
    }

}