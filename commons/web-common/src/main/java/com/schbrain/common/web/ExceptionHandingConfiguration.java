package com.schbrain.common.web;

import com.schbrain.common.web.exception.*;
import com.schbrain.common.web.properties.WebProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaozan
 * @since 2023-05-08
 */
@Configuration(proxyBeanMethods = false)
public class ExceptionHandingConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler defaultGlobalExceptionHandler() {
        return new DefaultGlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(GlobalExceptionHandler.class)
    public ExceptionHandlerWebMcvConfigurer defaultExceptionHandlerWebMcvConfigurer(WebProperties webProperties, GlobalExceptionHandler exceptionHandler) {
        return new ExceptionHandlerWebMcvConfigurer(webProperties, exceptionHandler);
    }

}