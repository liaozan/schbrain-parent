package com.schbrain.framework.autoconfigure.logger.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.schbrain.framework.autoconfigure.logger.apollo.listener.LoggingLevelChangeListener;
import com.schbrain.framework.autoconfigure.logger.properties.LoggerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.util.StringUtils;

/**
 * 动态日志配置
 *
 * @author liaozan
 * @since 2021/11/19
 **/
@Slf4j
public class DynamicLoggerConfiguration {

    public DynamicLoggerConfiguration(LoggingSystem loggingSystem, LoggerProperties loggerProperties) {
        this.listenToLoggingLevelChange(loggingSystem, loggerProperties);
    }

    private void listenToLoggingLevelChange(LoggingSystem loggingSystem, LoggerProperties loggerProperties) {
        String loggerNamespace = loggerProperties.getDefaultNamespace();
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