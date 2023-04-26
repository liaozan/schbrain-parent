package com.schbrain.framework.autoconfigure.logger.properties;

import com.google.common.collect.Maps;
import com.schbrain.common.util.InetUtils;
import com.schbrain.common.util.InetUtils.HostInfo;
import com.schbrain.framework.autoconfigure.apollo.properties.ApolloPropertiesPreparer;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import com.schbrain.framework.autoconfigure.logger.util.LoggerUtils;
import com.schbrain.framework.support.spring.EnvironmentPostProcessorAdapter;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.logging.*;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

import static com.schbrain.framework.autoconfigure.logger.util.LoggerUtils.getLoggerConfigurationLocation;
import static org.springframework.boot.context.logging.LoggingApplicationListener.CONFIG_PROPERTY;

/**
 * @author liaozan
 * @since 2021/11/19
 */
public class LoggerPropertiesPreparer extends EnvironmentPostProcessorAdapter implements Ordered {

    public LoggerPropertiesPreparer(DeferredLogFactory factory, ConfigurableBootstrapContext bootstrapContext) {
        super(factory, bootstrapContext);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        LoggerUtils.setLogFactory(getDeferredLogFactory());
        bindLoggingProperties(environment);
        bindHostInfoProperty(environment);
        earlyLoadLoggingConfig(environment);
    }

    @Override
    public int getOrder() {
        // Configure after the apollo property is initialize
        return ApolloPropertiesPreparer.ORDER + 1;
    }

    @Override
    public void onBootstrapContextClosed(ConfigurableApplicationContext applicationContext) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        if (!beanFactory.containsSingleton(HostInfo.NAME)) {
            HostInfo hostInfo = InetUtils.findFirstNonLoopBackHostInfo();
            beanFactory.registerSingleton(HostInfo.NAME, hostInfo);
        }
    }

    private void bindHostInfoProperty(ConfigurableEnvironment environment) {
        HostInfo hostInfo = InetUtils.findFirstNonLoopBackHostInfo();
        Map<String, Object> source = Maps.newHashMapWithExpectedSize(2);
        source.put("application.hostname", hostInfo.getHostname());
        source.put("application.ipAddress", hostInfo.getIpAddress());
        ConfigUtils.addToEnvironment(environment, HostInfo.NAME, source);
    }

    /**
     * @see LoggingApplicationListener#setLogLevels(LoggingSystem, ConfigurableEnvironment)
     */
    @SuppressWarnings("JavadocReference")
    private void earlyLoadLoggingConfig(ConfigurableEnvironment environment) {
        LoggingNamespaceProperties properties = ConfigUtils.loadConfig(environment, LoggingNamespaceProperties.class);
        // load logging level properties
        Map<String, Object> loggingLevelProperties = ConfigUtils.loadConfig(properties.getLogger());
        ConfigUtils.addToEnvironment(environment, "loggingLevelProperties", loggingLevelProperties);
        // load logging file properties
        ConfigUtils.loadConfig(environment, LoggingFileProperties.class);
    }

    /**
     * Add {@link LoggingApplicationListener#CONFIG_PROPERTY} property to SystemProperty
     *
     * @see LoggingApplicationListener#initializeSystem(ConfigurableEnvironment, LoggingSystem, LogFile)
     */
    @SuppressWarnings("JavadocReference")
    private void bindLoggingProperties(ConfigurableEnvironment environment) {
        if (environment.containsProperty(CONFIG_PROPERTY)) {
            return;
        }
        String loggerConfigurationLocation = getLoggerConfigurationLocation(environment);
        if (loggerConfigurationLocation == null) {
            return;
        }
        System.setProperty(CONFIG_PROPERTY, loggerConfigurationLocation);
        getLog().debug(String.format("%s is set to %s", CONFIG_PROPERTY, loggerConfigurationLocation));
    }

}