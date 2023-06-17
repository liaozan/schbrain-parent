package com.schbrain.common.web.exception;

import com.schbrain.common.web.properties.WebProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.List;

/**
 * @author liaozan
 * @since 2022/8/29
 */
@Slf4j
public class ExceptionHandingWebMvcConfigurer implements WebMvcConfigurer {

    private final WebProperties webProperties;

    private final GlobalExceptionHandler globalExceptionHandler;

    public ExceptionHandingWebMvcConfigurer(WebProperties webProperties, GlobalExceptionHandler globalExceptionHandler) {
        this.webProperties = webProperties;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        if (!webProperties.isEnableGlobalExceptionHandler()) {
            log.warn("Global exception handing is disabled");
            return;
        }

        ExceptionHandlerExceptionResolver adviceExceptionResolver = null;
        for (HandlerExceptionResolver resolver : resolvers) {
            if (resolver instanceof ExceptionHandlerExceptionResolver) {
                adviceExceptionResolver = (ExceptionHandlerExceptionResolver) resolver;
                break;
            }
        }

        if (adviceExceptionResolver == null) {
            log.warn("ExceptionHandlerExceptionResolver is not exist, ignore global exception handing");
            return;
        }

        int index = resolvers.indexOf(adviceExceptionResolver) + 1;
        resolvers.add(index, createExceptionResolver(adviceExceptionResolver));
    }

    protected HandlerExceptionResolver createExceptionResolver(ExceptionHandlerExceptionResolver adviceExceptionResolver) {
        return new DefaultGlobalExceptionResolver(adviceExceptionResolver, webProperties, globalExceptionHandler);
    }

}