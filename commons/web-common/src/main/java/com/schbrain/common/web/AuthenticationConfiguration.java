package com.schbrain.common.web;

import com.schbrain.common.web.support.authentication.AuthenticationInterceptor;
import com.schbrain.common.web.support.authentication.Authenticator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaozan
 * @since 2023-05-08
 */
@Configuration(proxyBeanMethods = false)
public class AuthenticationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(Authenticator.class)
    public AuthenticationInterceptor defaultAuthenticationInterceptor(Authenticator authenticator) {
        return new AuthenticationInterceptor(authenticator);
    }

}
