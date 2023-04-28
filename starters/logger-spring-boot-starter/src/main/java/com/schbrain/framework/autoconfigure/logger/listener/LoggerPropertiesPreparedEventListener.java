package com.schbrain.framework.autoconfigure.logger.listener;

import cn.hutool.system.SystemUtil;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.google.common.collect.Maps;
import com.schbrain.common.util.InetUtils;
import com.schbrain.common.util.InetUtils.HostInfo;
import com.schbrain.framework.autoconfigure.apollo.listener.PropertiesPreparedEvent;
import com.schbrain.framework.autoconfigure.apollo.listener.PropertiesPreparedEventListenerAdapter;
import com.schbrain.framework.autoconfigure.logger.LoggerConfigurationInitializer;
import com.schbrain.framework.autoconfigure.logger.properties.LoggerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

import static org.springframework.boot.context.logging.LoggingApplicationListener.CONFIG_PROPERTY;

/**
 * @author liaozan
 * @since 2023-04-28
 */
@Slf4j
public class LoggerPropertiesPreparedEventListener extends PropertiesPreparedEventListenerAdapter<LoggerProperties> {

    @Override
    protected void onPropertiesPrepared(PropertiesPreparedEvent event, LoggerProperties properties) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Map<String, String> hostInfoProperties = buildHostInfoProperties();
        event.getPropertySource().addProperties(hostInfoProperties);
        configLoggingFileLocation(environment);
        new LoggerConfigurationInitializer(environment, properties).init();
    }

    private Map<String, String> buildHostInfoProperties() {
        HostInfo hostInfo = InetUtils.findFirstNonLoopBackHostInfo();
        Map<String, String> properties = Maps.newHashMapWithExpectedSize(2);
        properties.put("application.hostname", hostInfo.getHostname());
        properties.put("application.ipAddress", hostInfo.getIpAddress());
        return properties;
    }

    /**
     * Add {@link org.springframework.boot.context.logging.LoggingApplicationListener#CONFIG_PROPERTY} property to SystemProperty
     *
     * @see org.springframework.boot.context.logging.LoggingApplicationListener#initializeSystem(ConfigurableEnvironment, org.springframework.boot.logging.LoggingSystem, org.springframework.boot.logging.LogFile)
     */
    @SuppressWarnings("JavadocReference")
    private void configLoggingFileLocation(ConfigurableEnvironment environment) {
        if (environment.containsProperty(CONFIG_PROPERTY)) {
            return;
        }
        ConfigFile loggingConfiguration = ConfigService.getConfigFile("logback-spring", ConfigFileFormat.XML);
        String content = loggingConfiguration.getContent();
        if (!StringUtils.hasText(content)) {
            log.warn("empty logging configuration, reinitialize loggingSystem is disabled");
            return;
        }

        String loggerConfigurationLocation = null;
        String tempDir = SystemUtil.getUserInfo().getTempDir();
        Path storeLocation = Paths.get(tempDir, "logback-spring.xml");
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