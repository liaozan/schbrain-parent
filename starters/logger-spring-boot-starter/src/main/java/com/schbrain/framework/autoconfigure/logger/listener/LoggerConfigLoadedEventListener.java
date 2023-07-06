package com.schbrain.framework.autoconfigure.logger.listener;

import cn.hutool.core.text.StrPool;
import cn.hutool.system.SystemUtil;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.google.common.collect.Maps;
import com.schbrain.common.util.InetUtils;
import com.schbrain.common.util.InetUtils.HostInfo;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.GenericConfigLoadedEventListener;
import com.schbrain.framework.autoconfigure.logger.LoggerConfigurationInitializer;
import com.schbrain.framework.autoconfigure.logger.properties.LoggerProperties;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.springframework.boot.context.logging.LoggingApplicationListener.CONFIG_PROPERTY;

/**
 * @author liaozan
 * @since 2023-04-28
 */
public class LoggerConfigLoadedEventListener extends GenericConfigLoadedEventListener<LoggerProperties> {

    private LoggerConfigurationInitializer loggerInitializer;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        loggerInitializer.init();
    }

    @Override
    protected void onConfigLoaded(ConfigLoadedEvent event, LoggerProperties properties) {
        HostInfo hostInfo = InetUtils.findFirstNonLoopBackHostInfo();
        Map<String, String> hostInfoProperties = buildHostInfoProperties(hostInfo);
        event.getPropertySource().addProperties(hostInfoProperties);
        configLoggingFileLocation(event.getEnvironment(), properties.getLogConfigNamespace());
        this.loggerInitializer = new LoggerConfigurationInitializer(event.getEnvironment(), properties, hostInfo);
    }

    /**
     * hostInfo properties, for logging pattern
     */
    private Map<String, String> buildHostInfoProperties(HostInfo hostInfo) {
        Map<String, String> properties = Maps.newHashMapWithExpectedSize(2);
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
        if (!StringUtils.hasText(content)) {
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