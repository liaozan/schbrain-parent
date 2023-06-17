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
    public GlobalExceptionHandler defaultGlobalExceptionHandler(ObjectProvider<ExceptionTranslator> exceptionTranslators) {
        return new GlobalExceptionHandler(exceptionTranslators.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandingWebMvcConfigurer defaultExceptionHandingWebMvcConfigurer(WebProperties webProperties, GlobalExceptionHandler exceptionHandler) {
        return new ExceptionHandingWebMvcConfigurer(webProperties, exceptionHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(GlobalExceptionHandler.class)
    public ExceptionTranslator defaultExceptionTranslator() {
        return new DefaultExceptionTranslator();
    }

}