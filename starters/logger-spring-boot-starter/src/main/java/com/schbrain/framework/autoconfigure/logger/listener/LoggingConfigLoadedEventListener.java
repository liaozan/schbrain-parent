package com.schbrain.framework.autoconfigure.logger.listener;

import cn.hutool.core.text.StrPool;
import cn.hutool.system.SystemUtil;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.google.common.collect.Maps;
import com.schbrain.common.util.HostInfoHolder;
import com.schbrain.common.util.HostInfoHolder.HostInfo;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.ConfigLoadedEventListenerAdaptor;
import com.schbrain.framework.autoconfigure.logger.JSONLoggingInitializer;
import com.schbrain.framework.autoconfigure.logger.properties.LoggingProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

import static org.springframework.boot.context.logging.LoggingApplicationListener.CONFIG_PROPERTY;

/**
 * @author liaozan
 * @since 2023-04-28
 */
public class LoggingConfigLoadedEventListener extends ConfigLoadedEventListenerAdaptor<LoggingProperties> {

    @Override
    protected void onConfigLoaded(ConfigLoadedEvent event, LoggingProperties properties) {
        event.getPropertySource().addProperties(buildHostInfoProperties());
        configLoggingFileLocation(event.getEnvironment(), properties.getLogConfigNamespace());
    }

    @Override
    protected void onApplicationContextInitialized(ConfigurableApplicationContext context, LoggingProperties properties) {
        JSONLoggingInitializer.init(context.getEnvironment(), properties);
    }

    /**
     * hostInfo properties, for logging pattern, used in logback-spring.xml
     */
    private Map<String, Object> buildHostInfoProperties() {
        HostInfo hostInfo = HostInfoHolder.getHostInfo();
        Map<String, Object> properties = Maps.newHashMapWithExpectedSize(2);
        properties.put("application.hostname", hostInfo.getHostname());
        properties.put("application.ipAddress", hostInfo.getIpAddress());
        return properties;
    }

    /**
     * Add {@link LoggingApplicationListener#CONFIG_PROPERTY} property to SystemProperty
     *
     * @see LoggingApplicationListener#initializeSystem(ConfigurableEnvironment, LoggingSystem, LogFile)
     */
    @SuppressWarnings("JavadocReference")
    private void configLoggingFileLocation(ConfigurableEnvironment environment, String logConfigNamespace) {
        if (environment.containsProperty(CONFIG_PROPERTY)) {
            return;
        }
        ConfigFile loggingConfiguration = ConfigService.getConfigFile(logConfigNamespace, ConfigFileFormat.XML);
        String content = loggingConfiguration.getContent();
        if (StringUtils.isBlank(content)) {
            log.warn("empty logging configuration, reinitialize loggingSystem is disabled");
            return;
        }

        String loggerConfigurationLocation = null;
        String tempDir = SystemUtil.getUserInfo().getTempDir();
        Path storeLocation = Paths.get(tempDir, logConfigNamespace + StrPool.DOT + ConfigFileFormat.XML.getValue());
        try {
            loggerConfigurationLocation = Files.writeString(storeLocation, content).toString();
        } catch (IOException e) {
            log.warn("failed to write logging file, will not behave as expected", e);
        }

        if (loggerConfigurationLocation == null) {
            return;
        }
        System.setProperty(CONFIG_PROPERTY, loggerConfigurationLocation);
        log.debug(String.format("%s is set to %s", CONFIG_PROPERTY, loggerConfigurationLocation));
    }

}
