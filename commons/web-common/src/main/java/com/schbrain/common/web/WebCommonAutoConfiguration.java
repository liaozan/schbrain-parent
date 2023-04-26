package com.schbrain.common.web;

import com.schbrain.common.web.argument.BodyParamArgumentResolverWebMvcConfigurer;
import com.schbrain.common.web.exception.*;
import com.schbrain.common.web.log.RequestLoggingFilter;
import com.schbrain.common.web.properties.WebProperties;
import com.schbrain.common.web.result.ResponseBodyHandler;
import com.schbrain.common.web.servlet.*;
import com.schbrain.common.web.support.authentication.AuthenticationInterceptor;
import com.schbrain.common.web.support.authentication.Authenticator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author liaozan
 * @since 2021/11/19
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties(WebProperties.class)
public class WebCommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Authenticator.class)
    public AuthenticationInterceptor defaultAuthenticationInterceptor(Authenticator authenticator) {
        return new AuthenticationInterceptor(authenticator);
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler defaultGlobalExceptionHandler() {
        return new DefaultGlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerWebMcvConfigurer defaultExceptionHandlerWebMcvConfigurer(WebProperties webProperties, GlobalExceptionHandler exceptionHandler) {
        return new ExceptionHandlerWebMcvConfigurer(webProperties, exceptionHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    public BodyParamArgumentResolverWebMvcConfigurer defaultBodyParamArgumentResolverWebMvcConfigurer() {
        return new BodyParamArgumentResolverWebMvcConfigurer();
    }

    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(ObjectProvider<RestTemplateBuilder> restTemplateBuilder) {
        RestTemplateBuilder builder = restTemplateBuilder.getIfAvailable();
        if (builder == null) {
            return new RestTemplate();
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseBodyHandler defaultResponseBodyHandler(WebProperties properties, BeanFactory beanFactory) {
        List<String> basePackages = AutoConfigurationPackages.get(beanFactory);
        return new ResponseBodyHandler(properties, basePackages);
    }

    @Bean
    @ConditionalOnMissingBean
    public TraceIdInitializeServletListener traceIdInitializeServletListener() {
        return new TraceIdInitializeServletListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public CharacterEncodingServletContextInitializer characterEncodingServletContextInitializer(WebProperties webProperties) {
        return new CharacterEncodingServletContextInitializer(webProperties.getEncoding());
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestLoggingFilter requestLoggingFilter(WebProperties properties) {
        return new RequestLoggingFilter(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AllowAllCorsConfigurer allowAllCorsConfigurer() {
        return new AllowAllCorsConfigurer();
    }

}