package com.schbrain.common.web;

import com.schbrain.common.web.exception.DefaultExceptionTranslator;
import com.schbrain.common.web.exception.ExceptionHandingWebMvcConfigurer;
import com.schbrain.common.web.exception.ExceptionTranslator;
import com.schbrain.common.web.exception.GlobalExceptionHandler;
import com.schbrain.common.web.properties.WebProperties;
import com.schbrain.common.web.result.ResponseDTO;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

/**
 * @author liaozan
 * @since 2023-05-08
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "schbrain.web.enable-global-exception-handler", havingValue = "true", matchIfMissing = true)
public class ExceptionHandingConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExceptionTranslator<ResponseDTO<Void>> defaultExceptionTranslator() {
        return new DefaultExceptionTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler defaultGlobalExceptionHandler(ObjectProvider<ExceptionTranslator<?>> exceptionTranslators) {
        return new GlobalExceptionHandler(exceptionTranslators.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandingWebMvcConfigurer defaultExceptionHandingWebMvcConfigurer(WebProperties webProperties, GlobalExceptionHandler exceptionHandler) {
        return new ExceptionHandingWebMvcConfigurer(webProperties, exceptionHandler);
    }

}
