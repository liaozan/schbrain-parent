package com.schbrain.framework.autoconfigure.cache.provider.redis;

import com.schbrain.framework.autoconfigure.cache.provider.CacheProvider;
import org.springframework.boot.autoconfigure.condition.*;
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
public class SchbrainRedisCacheConfiguration {

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnMissingBean(RedisCacheProvider.class)
    public CacheProvider schbrainRedisCacheProvider(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        return new RedisCacheProvider(stringRedisTemplate);
    }

}