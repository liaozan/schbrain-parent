package com.schbrain.framework.autoconfigure.logger.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.schbrain.framework.autoconfigure.logger.properties.LoggingProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.logging.LoggingSystem;

/**
 * 动态日志配置
 *
 * @author liaozan
 * @since 2021/11/19
 **/
@Slf4j
public class DynamicLoggingConfiguration {

    public DynamicLoggingConfiguration(LoggingSystem loggingSystem, LoggingProperties loggingProperties) {
        this.listenToLoggingLevelChange(loggingSystem, loggingProperties);
    }

    private void listenToLoggingLevelChange(LoggingSystem loggingSystem, LoggingProperties loggingProperties) {
        String loggingNamespace = loggingProperties.getNamespace();
        if (StringUtils.isBlank(loggingNamespace)) {
            log.debug("logging level reload is disabled");
            return;
        }

        log.debug("init logging level listener, logging namespace: {}", loggingNamespace);

        Config config = ConfigService.getConfig(loggingNamespace);
        if (config == null) {
            return;
        }
        config.addChangeListener(new LoggingLevelChangeListener(loggingSystem));
    }

}
