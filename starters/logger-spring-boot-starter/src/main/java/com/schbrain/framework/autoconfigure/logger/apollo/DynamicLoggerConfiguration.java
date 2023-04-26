package com.schbrain.framework.autoconfigure.logger.apollo;

import com.ctrip.framework.apollo.*;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import com.schbrain.framework.autoconfigure.logger.apollo.listener.LoggingConfigFileChangeListener;
import com.schbrain.framework.autoconfigure.logger.apollo.listener.LoggingLevelChangeListener;
import com.schbrain.framework.autoconfigure.logger.properties.LoggingNamespaceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

/**
 * 动态日志配置
 *
 * @author liaozan
 * @since 2021/11/19
 **/
@Slf4j
@EnableConfigurationProperties(LoggingNamespaceProperties.class)
public class DynamicLoggerConfiguration {

    private final ConfigurableEnvironment environment;
    private final LoggingSystem loggingSystem;
    private final LoggingNamespaceProperties loggerProperties;

    public DynamicLoggerConfiguration(ConfigurableEnvironment environment, LoggingSystem loggingSystem,
                                      LoggingNamespaceProperties loggerProperties) {
        this.environment = environment;
        this.loggingSystem = loggingSystem;
        this.loggerProperties = loggerProperties;
        this.init();
    }

    private void init() {
        if (ConfigUtils.isApolloDisabled()) {
            return;
        }
        listenToLoggingLevelChange();
        listenToLoggingConfigFileChange();
    }

    private void listenToLoggingConfigFileChange() {
        String loggerConfigFileNamespace = loggerProperties.getLoggerConfigFile();
        if (!StringUtils.hasText(loggerConfigFileNamespace)) {
            log.debug("logger config file reload is disabled");
            return;
        }

        log.debug("init logger config file listener, config file namespace: {}", loggerConfigFileNamespace);

        ConfigFile loggingConfiguration = ConfigService.getConfigFile(loggerConfigFileNamespace, ConfigFileFormat.XML);
        if (!loggingConfiguration.hasContent()) {
            return;
        }
        loggingConfiguration.addChangeListener(new LoggingConfigFileChangeListener(loggingSystem, environment, loggerConfigFileNamespace));
    }

    private void listenToLoggingLevelChange() {
        String loggerNamespace = loggerProperties.getLogger();
        if (!StringUtils.hasText(loggerNamespace)) {
            log.debug("logger level reload is disabled");
            return;
        }

        log.debug("init logger level listener, logger namespace: {}", loggerNamespace);

        Config config = ConfigService.getConfig(loggerNamespace);
        if (config == null) {
            return;
        }
        config.addChangeListener(new LoggingLevelChangeListener(loggingSystem));
    }

}