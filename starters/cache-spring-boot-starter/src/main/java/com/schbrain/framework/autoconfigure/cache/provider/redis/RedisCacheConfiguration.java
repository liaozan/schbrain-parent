package com.schbrain.framework.autoconfigure.cache.provider.redis;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.*;
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
    @ConditionalOnMissingBean(RedisCacheOperation.class)
    public RedisCacheOperation redisCacheOperation(RedisConnectionFactory factory, ObjectProvider<StringRedisTemplate> redisTemplate) {
        StringRedisTemplate stringRedisTemplate = redisTemplate.getIfAvailable(() -> new StringRedisTemplate(factory));
        return new RedisCacheOperation(stringRedisTemplate);
    }

}
