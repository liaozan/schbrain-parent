package com.schbrain.framework.autoconfigure.logger.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author liaozan
 * @since 2021/12/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "schbrain.logging.file")
public class LoggingFileProperties extends ConfigurableProperties {

    public static final String DEFAULT_LOG_PATH = "/data/logs";

    private boolean enableJsonConsoleOutput = false;

    private boolean enableJsonFileOutput = false;

    private boolean enableJsonLogWriteToLogstash = false;

    private String logstashAddress;

    private String logPath = DEFAULT_LOG_PATH;

    private int maxHistory = (int) Duration.ofDays(30).toDays();

}