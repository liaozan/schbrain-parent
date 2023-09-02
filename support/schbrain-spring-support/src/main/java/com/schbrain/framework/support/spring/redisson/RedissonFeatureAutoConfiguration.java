package com.schbrain.framework.support.spring.redisson;

import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @author liaozan
 * @since 2023/8/25
 */
@AutoConfiguration
@ConditionalOnBean(RedissonClient.class)
@ConditionalOnClass(RedissonAutoConfigurationCustomizer.class)
public class RedissonFeatureAutoConfiguration {

    @Bean
    public RedissonAutoConfigurationCustomizer singleServerConnectionMinimumIdleCustomizer() {
        return config -> {
            try {
                config.useSingleServer().setConnectionMinimumIdleSize(10);
            } catch (IllegalStateException ignore) {
            }
        };
    }

}
