package com.schbrain.framework.autoconfigure.logger;

import com.schbrain.framework.autoconfigure.logger.apollo.DynamicLoggerConfiguration;
import com.schbrain.framework.autoconfigure.logger.properties.LoggerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * @author liaozan
 * @since 2021/11/19
 */
@AutoConfiguration
@Import(DynamicLoggerConfiguration.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(LoggerProperties.class)
public class LoggerAutoConfiguration {

}