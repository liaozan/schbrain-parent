package com.schbrain.framework.autoconfigure.logger;

import com.schbrain.framework.autoconfigure.logger.apollo.DynamicLoggingConfiguration;
import com.schbrain.framework.autoconfigure.logger.properties.LoggingProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @author liaozan
 * @since 2021/11/19
 */
@AutoConfiguration
@Import(DynamicLoggingConfiguration.class)
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingAutoConfiguration {

}
