package com.schbrain.framework.autoconfigure.starrocks;

import com.schbrain.framework.autoconfigure.starrocks.operation.StarrocksOperationFactory;
import com.schbrain.framework.autoconfigure.starrocks.properties.StarrocksProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author liaozan
 * @since 2023/11/27
 */
@AutoConfiguration
@EnableConfigurationProperties(StarrocksProperties.class)
public class StarrocksAutoConfiguration {

    @Bean
    public StarrocksOperationFactory starrocksOperationFactory(StarrocksProperties starrocksProperties) {
        return new StarrocksOperationFactory(starrocksProperties);
    }

}
