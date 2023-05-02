package com.schbrain.framework.autoconfigure.oss;

import com.schbrain.framework.autoconfigure.oss.properties.OssProperties;
import com.schbrain.framework.autoconfigure.oss.util.OssUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author liaozan
 * @since 2021/12/3
 */
@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    public OssAutoConfiguration(ConfigurableApplicationContext applicationContext, OssProperties ossProperties) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        OssUtils.initialize(environment, ossProperties);
    }

}