package com.schbrain.framework.autoconfigure.xxl;

import com.schbrain.common.util.ApplicationName;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import com.schbrain.framework.autoconfigure.logger.properties.LoggingFileProperties;
import com.schbrain.framework.autoconfigure.xxl.condition.XxlJobShouldAvailableCondition;
import com.schbrain.framework.autoconfigure.xxl.properties.XxlJobProperties;
import com.xxl.job.core.executor.XxlJobExecutor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.ConfigurableEnvironment;

import java.nio.file.Paths;

/**
 * @author liaozan
 * @since 2022/1/8
 */
@AutoConfiguration
@Conditional(XxlJobShouldAvailableCondition.class)
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(XxlJobExecutor.class)
    public SchbrainXxlJobExecutor schbrainXxlJobSpringExecutor(ConfigurableEnvironment environment) {
        XxlJobProperties xxlJobProperties = ConfigUtils.loadConfig(environment, XxlJobProperties.class);
        LoggingFileProperties loggingProperties = ConfigUtils.loadConfig(environment, LoggingFileProperties.class);
        String applicationName = ApplicationName.get(environment);
        SchbrainXxlJobExecutor executor = new SchbrainXxlJobExecutor();
        executor.setAdminAddresses(xxlJobProperties.getAdminAddresses());
        executor.setIp(xxlJobProperties.getIp());
        executor.setPort(xxlJobProperties.getPort());
        executor.setAppName(applicationName);
        executor.setAccessToken(xxlJobProperties.getAccessToken());
        executor.setLogPath(Paths.get(loggingProperties.getLogPath(), "xxl-job").toString());
        executor.setLogRetentionDays(xxlJobProperties.getLogRetentionDays());
        return executor;
    }

}