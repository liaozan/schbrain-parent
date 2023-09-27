package com.schbrain.common.web.support.converter.datetime;

import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liaozan
 * @since 2023/8/16
 */
public class DateTimeConvertersWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(StringToDateConditionalConverter.INSTANCE);
        registry.addConverter(StringToLocalTimeConditionalConverter.INSTANCE);
        registry.addConverter(StringToLocalDateConditionalConverter.INSTANCE);
        registry.addConverter(StringToLocalDateTimeConditionalConverter.INSTANCE);
    }

}
