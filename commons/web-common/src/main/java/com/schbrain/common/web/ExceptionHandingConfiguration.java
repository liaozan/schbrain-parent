package com.schbrain.common.web;

import com.schbrain.common.web.exception.*;
import com.schbrain.common.web.properties.WebProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

/**
 * @author liaozan
 * @since 2023-05-08
 */
@Configuration(proxyBeanMethods = false)
public class ExceptionHandingConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler defaultGlobalExceptionHandler(ObjectProvider<ExceptionTranslator> exceptionTranslators) {
        return new DefaultGlobalExceptionHandler(exceptionTranslators.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(GlobalExceptionHandler.class)
    public ExceptionTranslator defaultExceptionTranslator() {
        return new DefaultExceptionTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(GlobalExceptionHandler.class)
    public ExceptionHandlerWebMcvConfigurer defaultExceptionHandlerWebMcvConfigurer(WebProperties webProperties, GlobalExceptionHandler exceptionHandler) {
        return new ExceptionHandlerWebMcvConfigurer(webProperties, exceptionHandler);
    }

}