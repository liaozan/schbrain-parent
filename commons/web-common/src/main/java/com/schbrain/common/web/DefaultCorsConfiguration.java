package com.schbrain.common.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;

/**
 * @author liaozan
 * @since 2023-06-26
 */
@Configuration(proxyBeanMethods = false)
public class DefaultCorsConfiguration {

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
    @ConditionalOnMissingBean
    public CorsFilter defaultCorsFilter(UrlBasedCorsConfigurationSource corsConfigurationSource) {
        return new CorsFilter(corsConfigurationSource);
    }

}