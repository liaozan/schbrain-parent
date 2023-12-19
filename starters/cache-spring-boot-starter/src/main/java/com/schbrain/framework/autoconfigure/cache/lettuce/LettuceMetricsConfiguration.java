package com.schbrain.framework.autoconfigure.cache.lettuce;

import io.lettuce.core.metrics.MicrometerOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.*;

/**
 * @author liaozan
 * @since 2023/12/19
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MicrometerOptions.class)
public class LettuceMetricsConfiguration {

    @Bean
    @Primary
    public MicrometerOptions disableLettuceMetrics() {
        return MicrometerOptions.disabled();
    }

}
