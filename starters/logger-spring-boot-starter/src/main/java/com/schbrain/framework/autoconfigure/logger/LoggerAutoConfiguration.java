package com.schbrain.framework.autoconfigure.logger;

import com.schbrain.framework.autoconfigure.logger.apollo.DynamicLoggerConfiguration;
import com.schbrain.framework.autoconfigure.logger.properties.LoggerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @author liaozan
 * @since 2021/11/19
 */
@AutoConfiguration
@Import(DynamicLoggerConfiguration.class)
@EnableConfigurationProperties(LoggerProperties.class)
public class LoggerAutoConfiguration {

}