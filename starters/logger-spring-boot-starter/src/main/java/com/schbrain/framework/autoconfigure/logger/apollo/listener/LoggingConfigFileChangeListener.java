package com.schbrain.framework.autoconfigure.logger.apollo.listener;

import cn.hutool.extra.spring.SpringUtil;
import com.ctrip.framework.apollo.ConfigFileChangeListener;
import com.ctrip.framework.apollo.model.ConfigFileChangeEvent;
import com.schbrain.framework.autoconfigure.logger.JsonLoggerInitializer;
import com.schbrain.framework.autoconfigure.logger.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.*;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author liaozan
 * @since 2021/11/8
 */
@Slf4j
public class LoggingConfigFileChangeListener implements ConfigFileChangeListener {

    private final LoggingSystem loggingSystem;
    private final ConfigurableEnvironment environment;
    private final String loggerFileName;

    public LoggingConfigFileChangeListener(LoggingSystem loggingSystem, ConfigurableEnvironment environment, String loggerFileName) {
        this.loggingSystem = loggingSystem;
        this.environment = environment;
        this.loggerFileName = loggerFileName;
    }

    @Override
    public void onChange(ConfigFileChangeEvent changeEvent) {
        String content = changeEvent.getNewValue();
        if (!StringUtils.hasText(content)) {
            log.warn("Empty logging configuration, reInitialize loggingSystem is disabled");
            return;
        }
        String configurationLocation = LoggerUtils.storeConfiguration(loggerFileName, content);
        if (configurationLocation == null) {
            return;
        }
        reinitialize(configurationLocation);
        log.debug("ReInitialize loggingSystem, configFile location: {}", configurationLocation);
    }

    private void reinitialize(String configLocation) {
        List<LoggerConfiguration> configurations = loggingSystem.getLoggerConfigurations();
        loggingSystem.cleanUp();
        loggingSystem.initialize(new LoggingInitializationContext(environment), configLocation, null);
        configurations.forEach(configuration -> loggingSystem.setLogLevel(configuration.getName(), configuration.getConfiguredLevel()));
        // reInitialize json logger
        new JsonLoggerInitializer().initialize(SpringUtil.getBean(ConfigurableApplicationContext.class));
    }

}