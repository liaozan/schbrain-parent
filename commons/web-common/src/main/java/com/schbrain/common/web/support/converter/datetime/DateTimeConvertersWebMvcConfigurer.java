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
        registry.addConverter(StringToDateConverter.INSTANCE);
        registry.addConverter(StringToLocalTimeConverter.INSTANCE);
        registry.addConverter(StringToLocalDateConverter.INSTANCE);
        registry.addConverter(StringToLocalDateTimeConverter.INSTANCE);
    }

}
