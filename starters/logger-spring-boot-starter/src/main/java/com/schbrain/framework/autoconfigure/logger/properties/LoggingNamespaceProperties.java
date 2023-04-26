package com.schbrain.framework.autoconfigure.logger.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2021/11/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "schbrain.logging.namespace")
public class LoggingNamespaceProperties extends ConfigurableProperties {

    private String logger = "logger-common";

    private String loggerConfigFile = "logback-spring";

}