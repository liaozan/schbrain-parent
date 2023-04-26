package com.schbrain.framework.autoconfigure.logger.util;

import cn.hutool.system.SystemUtil;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import com.schbrain.framework.autoconfigure.logger.properties.LoggingNamespaceProperties;
import org.apache.commons.logging.Log;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * @author liaozan
 * @since 2021/11/15
 */
public class LoggerUtils {

    private static Log LOGGER;

    public static void setLogFactory(DeferredLogFactory deferredLog) {
        LOGGER = deferredLog.getLog(LoggerUtils.class);
    }

    @Nullable
    public static String getLoggerConfigurationLocation(ConfigurableEnvironment environment) {
        if (ConfigUtils.isApolloDisabled()) {
            return null;
        }
        LoggingNamespaceProperties properties = ConfigUtils.loadConfig(environment, LoggingNamespaceProperties.class);
        String namespace = properties.getLoggerConfigFile();
        ConfigFile loggingConfiguration = ConfigService.getConfigFile(namespace, ConfigFileFormat.XML);
        String content = loggingConfiguration.getContent();
        if (!StringUtils.hasText(content)) {
            LOGGER.warn("empty logging configuration, reinitialize loggingSystem is disabled");
            return null;
        }
        return storeConfiguration(namespace, content);
    }

    @Nullable
    public static String storeConfiguration(String fileName, String content) {
        String tempDir = SystemUtil.getUserInfo().getTempDir();
        Path storeLocation = Paths.get(tempDir, fileName + ".xml");
        try {
            return Files.write(storeLocation, content.getBytes(StandardCharsets.UTF_8)).toString();
        } catch (IOException e) {
            LOGGER.warn("failed to write logging file, will not behave as expected", e);
            return null;
        }
    }

}