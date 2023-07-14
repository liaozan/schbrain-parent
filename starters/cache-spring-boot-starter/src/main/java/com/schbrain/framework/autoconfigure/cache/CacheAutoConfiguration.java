package com.schbrain.framework.autoconfigure.cache;

import com.schbrain.common.util.JacksonUtils;
import com.schbrain.framework.autoconfigure.cache.concurrent.RateLimitAspect;
import com.schbrain.framework.autoconfigure.cache.properties.CacheProperties;
import com.schbrain.framework.autoconfigure.cache.provider.CacheProvider;
import com.schbrain.framework.autoconfigure.cache.provider.CacheProviderDelegate;
import com.schbrain.framework.autoconfigure.cache.provider.redis.RedisCacheConfiguration;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnBean(CacheProvider.class)
    public CacheProvider cacheProvider(CacheProvider cacheProvider, CacheProperties cacheProperties) {
        CacheProvider provider = new CacheProviderDelegate(cacheProperties, cacheProvider);
        CacheUtils.setCacheProvider(provider);
        return provider;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedissonClient.class)
    public RateLimitAspect rateLimitAspect(ConfigurableListableBeanFactory beanFactory, RedissonClient redissonClient) {
        return new RateLimitAspect(beanFactory, redissonClient);
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public RedissonAutoConfigurationCustomizer redissonConfigurationCodecCustomizer() {
        return config -> config.setCodec(new JsonJacksonCodec(JacksonUtils.getObjectMapper()));
    }

}
