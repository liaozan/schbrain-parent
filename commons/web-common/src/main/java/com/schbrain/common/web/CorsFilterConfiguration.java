package com.schbrain.common.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;

/**
 * @author liaozan
 * @since 2023-06-26
 */
@Configuration(proxyBeanMethods = false)
public class CorsFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CorsConfiguration defaultCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addExposedHeader(CorsConfiguration.ALL);
        config.setMaxAge(Duration.ofDays(1));
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    public UrlBasedCorsConfigurationSource defaultCorsConfigurationSource(CorsConfiguration corsConfiguration) {
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", corsConfiguration);
        return configSource;
    }

    @Bean
    @ConditionalOnMissingFilterBean
    public FilterRegistrationBean<CorsFilter> defaultCorsFilter(UrlBasedCorsConfigurationSource configurationSource) {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorsFilter(configurationSource));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

}