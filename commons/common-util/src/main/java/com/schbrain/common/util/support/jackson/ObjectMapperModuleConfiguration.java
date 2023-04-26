package com.schbrain.common.util.support.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaozan
 * @since 2022/1/11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ObjectMapper.class)
public class ObjectMapperModuleConfiguration {

    @Bean
    public BlackbirdModule blackbirdModule() {
        return new BlackbirdModule();
    }

    @Bean
    @ConditionalOnMissingBean
    public JavaTimeModule javaTimeModule() {
        return new JavaTimeModule();
    }

}