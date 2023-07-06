package com.schbrain.framework.autoconfigure.logger.apollo.listener;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.logging.LoggingSystem;

import java.util.Set;

import static com.ctrip.framework.apollo.enums.PropertyChangeType.ADDED;
import static com.ctrip.framework.apollo.enums.PropertyChangeType.MODIFIED;
import static org.springframework.boot.logging.LoggingSystem.ROOT_LOGGER_NAME;

/**
 * @author liaozan
 * @since 2021/11/8
 */
@Slf4j
public class LoggingLevelChangeListener implements ConfigChangeListener {

    private static final String LOGGER_TAG = "logging.level.";

    private final LoggingSystem loggingSystem;

    public LoggingLevelChangeListener(LoggingSystem loggingSystem) {
        this.loggingSystem = loggingSystem;
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        Set<String> changedKeys = changeEvent.changedKeys();
        for (String key : changedKeys) {
            if (key.startsWith(LOGGER_TAG)) {
                String loggerName = key.substring(LOGGER_TAG.length());
                LogLevel newLogLevel = getNewLogLevel(changeEvent, key);
                configureLoggerLevel(loggerName, newLogLevel);
            }
        }
    }

    private LogLevel getNewLogLevel(ConfigChangeEvent changeEvent, String key) {
        // default is INFO
        LogLevel newLogLevel = LogLevel.INFO;
        ConfigChange configChange = changeEvent.getChange(key);
        PropertyChangeType changeType = configChange.getChangeType();
        if (changeType == ADDED || changeType == MODIFIED) {
            String newValue = configChange.getNewValue();
            newLogLevel = LogLevel.valueOf(newValue.toUpperCase());
        }
        return newLogLevel;
    }

    private void configureLoggerLevel(String loggerName, LogLevel logLevel) {
        LoggerConfiguration configuration = getLoggerConfiguration(loggerName);
        LogLevel configuredLevel = configuration.getConfiguredLevel();
        if (configuredLevel == logLevel) {
            return;
        }
        loggingSystem.setLogLevel(loggerName, logLevel);
        log.info("change [{}] logger level from {} to {}", loggerName, configuredLevel, logLevel);
    }

    private LoggerConfiguration getLoggerConfiguration(String loggerName) {
        LoggerConfiguration configuration = loggingSystem.getLoggerConfiguration(loggerName);
        if (configuration == null) {
            configuration = loggingSystem.getLoggerConfiguration(loggerName.toUpperCase());
        }
        if (configuration == null) {
            configuration = loggingSystem.getLoggerConfiguration(ROOT_LOGGER_NAME);
        }
        return configuration;
    }

}