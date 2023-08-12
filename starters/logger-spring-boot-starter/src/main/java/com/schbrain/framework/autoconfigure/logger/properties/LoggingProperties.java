package com.schbrain.framework.autoconfigure.logger.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.PriorityOrdered;

import java.time.Duration;

/**
 * @author liaozan
 * @since 2021/12/11
 */
@Data
@ConfigurationProperties(prefix = "schbrain.logging.file")
public class LoggingProperties implements ConfigurableProperties, PriorityOrdered {

    public static final String DEFAULT_LOG_PATH = "/data/logs";

    private boolean enableJsonConsoleOutput = false;

    private boolean enableJsonFileOutput = false;

    private boolean enableJsonLogWriteToLogstash = false;

    private String logstashAddress;

    private String logConfigNamespace = "logback-spring";

    private String logPath = DEFAULT_LOG_PATH;

    private int maxHistory = (int) Duration.ofDays(30).toDays();

    @Override
    public String getNamespace() {
        return "logger-common";
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
