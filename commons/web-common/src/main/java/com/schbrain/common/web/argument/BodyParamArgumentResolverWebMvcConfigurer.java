package com.schbrain.common.web.argument;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author liaozan
 * @since 2022-12-02
 */
public class BodyParamArgumentResolverWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new BodyParamMethodArgumentResolver());
    }

}
