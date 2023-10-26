package com.schbrain.framework.autoconfigure.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.*;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import cn.hutool.json.JSONObject;
import com.schbrain.common.util.*;
import com.schbrain.framework.autoconfigure.logger.logstash.EnhancedLogstashEncoder;
import com.schbrain.framework.autoconfigure.logger.properties.LoggingProperties;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.fieldnames.ShortenedFieldNames;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.nio.file.Paths;
import java.util.List;

/**
 * Enable the json logging, will be auto active when the application is running in CloudPlatform
 *
 * @author liaozan
 * @see CloudPlatform
 * @since 2021/12/11
 */
@Slf4j
public class JSONLoggingInitializer {

    private final String applicationName;
    private final Environment environment;
    private final LoggingProperties loggingProperties;

    private volatile boolean initialized;

    private JSONLoggingInitializer(Environment environment, LoggingProperties loggingProperties) {
        this.environment = environment;
        this.loggingProperties = loggingProperties;
        this.applicationName = ApplicationName.get(environment);
    }

    public static void init(Environment environment, LoggingProperties properties) {
        JSONLoggingInitializer initializer = new JSONLoggingInitializer(environment, properties);
        initializer.init();
    }

    public void init() {
        if (loggingProperties == null) {
            log.warn("logging properties is null");
            return;
        }
        if (initialized) {
            log.warn("{} is initialized", getClass().getName());
            return;
        }
        initialized = true;
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        for (Logger logger : context.getLoggerList()) {
            registerAppender(logger, context);
        }
    }

    private void registerAppender(Logger logger, LoggerContext context) {
        List<Appender<ILoggingEvent>> appenderList = getAppenderList(logger);
        if (CollectionUtils.isEmpty(appenderList)) {
            return;
        }

        if (loggingProperties.isEnableJsonFileOutput()) {
            Appender<ILoggingEvent> appender = buildFileAppender(context);
            logger.addAppender(appender);
        }

        if (loggingProperties.isEnableJsonConsoleOutput()) {
            Appender<ILoggingEvent> appender = buildConsoleAppender(context);
            logger.addAppender(appender);
        }

        if (loggingProperties.isEnableJsonLogWriteToLogstash() || EnvUtils.runningOnCloudPlatform(environment)) {
            if (StringUtils.isBlank(loggingProperties.getLogstashAddress())) {
                log.warn("logstash address is unset, will NOT write log to logstash");
                return;
            }
            Appender<ILoggingEvent> logstashAppender = buildLogstashAppender(context);
            logger.addAppender(logstashAppender);
        }
    }

    private Appender<ILoggingEvent> buildLogstashAppender(LoggerContext context) {
        LogstashTcpSocketAppender appender = new LogstashTcpSocketAppender();
        appender.setContext(context);
        appender.addDestination(loggingProperties.getLogstashAddress());
        appender.setEncoder(createJsonEncoder(context));
        appender.start();
        return appender;
    }

    private List<Appender<ILoggingEvent>> getAppenderList(Logger logger) {
        return IteratorUtils.toList(logger.iteratorForAppenders());
    }

    private Appender<ILoggingEvent> buildConsoleAppender(LoggerContext loggerContext) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setContext(loggerContext);
        appender.setEncoder(createJsonEncoder(loggerContext));
        appender.start();
        return appender;
    }

    private LogstashEncoder createJsonEncoder(LoggerContext loggerContext) {
        LogstashEncoder logstashEncoder = new EnhancedLogstashEncoder();
        logstashEncoder.setContext(loggerContext);
        logstashEncoder.setFieldNames(new ShortenedFieldNames());
        logstashEncoder.setShortenedLoggerNameLength(40);
        logstashEncoder.setCustomFields(getCustomFields());
        logstashEncoder.start();
        return logstashEncoder;
    }

    private String getCustomFields() {
        JSONObject customFields = new JSONObject();
        customFields.set("appName", applicationName);
        customFields.set("podIp", IpAddressHolder.getIpAddress());
        return customFields.toString();
    }

    private Appender<ILoggingEvent> buildFileAppender(LoggerContext loggerContext) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(loggerContext);
        appender.setFile(getPathLocation("json/json.log"));
        appender.setRollingPolicy(createRollingPolicy(loggerContext, appender));
        appender.setEncoder(createJsonEncoder(loggerContext));
        appender.start();
        return appender;
    }

    private String getPathLocation(String path) {
        return Paths.get(loggingProperties.getLogPath(), path).toString();
    }

    private TimeBasedRollingPolicy<ILoggingEvent> createRollingPolicy(Context context, FileAppender<ILoggingEvent> appender) {
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setMaxHistory(loggingProperties.getMaxHistory());
        rollingPolicy.setFileNamePattern(getPathLocation("json/json-%d{yyyy-MM-dd}.log"));
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(appender);
        rollingPolicy.start();
        return rollingPolicy;
    }

}
