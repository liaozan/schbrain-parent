package com.schbrain.framework.autoconfigure.apollo;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ctrip.framework.apollo.spring.boot.ApolloAutoConfiguration;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.schbrain.framework.autoconfigure.apollo.properties.ApolloProperties;
import com.schbrain.framework.autoconfigure.apollo.properties.ConfigurationPropertiesRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @author liaozan
 * @since 2021/11/15
 */
@EnableApolloConfig
@Import({SchbrainPropertySourcesProcessor.class, ConfigurationPropertiesRegistry.class})
@EnableConfigurationProperties(ApolloProperties.class)
@AutoConfiguration(before = ApolloAutoConfiguration.class)
@ConditionalOnProperty(value = PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, matchIfMissing = true)
public class SchbrainApolloAutoConfiguration {

}