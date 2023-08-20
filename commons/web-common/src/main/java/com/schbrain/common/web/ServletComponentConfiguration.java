package com.schbrain.common.web;

import com.schbrain.common.web.properties.WebProperties;
import com.schbrain.common.web.servlet.CharacterEncodingServletContextInitializer;
import com.schbrain.common.web.servlet.RequestLoggingFilter;
import com.schbrain.common.web.servlet.RequestWrapperFilter;
import com.schbrain.common.web.servlet.TraceIdInitializeServletListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;

/**
 * @author liaozan
 * @since 2023-05-08
 */
@Configuration(proxyBeanMethods = false)
public class ServletComponentConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TraceIdInitializeServletListener defaultTraceIdInitializeServletListener() {
        return new TraceIdInitializeServletListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public CharacterEncodingServletContextInitializer defaultCharacterEncodingServletContextInitializer(WebProperties webProperties) {
        return new CharacterEncodingServletContextInitializer(webProperties.getEncoding());
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestWrapperFilter defaukltRequestWrapperFilter() {
        return new RequestWrapperFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestContextFilter defaultRequestContextFilter() {
        OrderedRequestContextFilter requestContextFilter = new OrderedRequestContextFilter();
        requestContextFilter.setThreadContextInheritable(true);
        return requestContextFilter;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "schbrain.web.enable-request-logging", havingValue = "true", matchIfMissing = true)
    public RequestLoggingFilter defaultRequestLoggingFilter() {
        return new RequestLoggingFilter();
    }

}
