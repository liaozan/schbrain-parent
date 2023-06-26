package com.schbrain.common.web;

import com.schbrain.common.web.argument.BodyParamArgumentResolverWebMvcConfigurer;
import com.schbrain.common.web.properties.WebProperties;
import com.schbrain.common.web.result.ResponseBodyHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author liaozan
 * @since 2021/11/19
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(WebProperties.class)
@Import({AuthenticationConfiguration.class, ExceptionHandingConfiguration.class, ServletComponentConfiguration.class, DefaultCorsConfiguration.class})
public class WebCommonAutoConfiguration {

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
    @ConditionalOnProperty(value = "schbrain.web.wrap-response", havingValue = "true", matchIfMissing = true)
    public ResponseBodyHandler defaultResponseBodyHandler(BeanFactory beanFactory) {
        List<String> basePackages = AutoConfigurationPackages.get(beanFactory);
        return new ResponseBodyHandler(basePackages);
    }

}