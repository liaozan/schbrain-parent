package com.schbrain.framework.autoconfigure.dubbo;

import com.schbrain.framework.autoconfigure.dubbo.properties.DubboProperties;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import static org.apache.dubbo.config.ConfigKeys.DUBBO_SCAN_BASE_PACKAGES;

/**
 * @author liaozan
 * @since 2021/11/5
 */
@AutoConfiguration
@EnableConfigurationProperties(DubboProperties.class)
@EnableDubbo(scanBasePackages = "${" + DUBBO_SCAN_BASE_PACKAGES + "}")
public class DubboAutoConfiguration {

}