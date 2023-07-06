package com.schbrain.framework.autoconfigure.cache.provider.redis;

import com.schbrain.framework.autoconfigure.cache.provider.CacheProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Redis cache configuration
 *
 * @author liaozan
 * @since 2022/8/7
 */
@ConditionalOnClass(RedisConnectionFactory.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisCacheConfiguration {

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnMissingBean(RedisCacheProvider.class)
    public CacheProvider redisCacheProvider(RedisConnectionFactory redisConnectionFactory, ObjectProvider<StringRedisTemplate> redisTemplate) {
        StringRedisTemplate stringRedisTemplate = redisTemplate.getIfAvailable(() -> new StringRedisTemplate(redisConnectionFactory));
        return new RedisCacheProvider(stringRedisTemplate);
    }

}