package com.schbrain.common.web.servlet;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

/**
 * @author liaozan
 * @since 2022/11/19
 */
public class AllowAnyOriginWithoutCredentialsCorsConfigurer implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(false)
                .allowedOrigins(CorsConfiguration.ALL)
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                .exposedHeaders(CorsConfiguration.ALL)
                .maxAge(Duration.ofHours(1).toSeconds());
    }

}