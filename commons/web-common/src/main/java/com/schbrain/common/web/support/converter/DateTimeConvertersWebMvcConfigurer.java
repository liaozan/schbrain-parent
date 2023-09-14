package com.schbrain.common.web.support.converter;

import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liaozan
 * @since 2023/8/16
 */
public class DateTimeConvertersWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeConverters.getConverters().forEach(registry::addConverter);
    }

}
