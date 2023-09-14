package com.schbrain.common.web.support.converter;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liaozan
 * @since 2023/8/16
 */
class DateTimeConverters {

    static List<Converter<?, ?>> getConverters() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(DateConverter.INSTANCE);
        converters.add(StringToLocalDateConverter.INSTANCE);
        converters.add(StringToLocalTimeConverter.INSTANCE);
        converters.add(StringToLocalDateTimeConverter.INSTANCE);
        return converters;
    }

    private enum DateConverter implements Converter<String, Date> {

        INSTANCE;

        @Override
        public Date convert(String source) {
            if (NumberUtil.isLong(source)) {
                return DateUtil.date(Long.parseLong(source));
            } else {
                return DateUtil.parse(source, DatePattern.NORM_DATETIME_PATTERN);
            }
        }

    }

    private enum StringToLocalDateConverter implements Converter<String, LocalDate> {

        INSTANCE;

        @Override
        public LocalDate convert(String source) {
            return StringToLocalDateTimeConverter.INSTANCE.convert(source).toLocalDate();
        }
    }

    private enum StringToLocalTimeConverter implements Converter<String, LocalTime> {

        INSTANCE;

        @Override
        public LocalTime convert(String source) {
            return StringToLocalDateTimeConverter.INSTANCE.convert(source).toLocalTime();
        }
    }

    private enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

        INSTANCE;

        @NonNull
        @Override
        public LocalDateTime convert(String source) {
            if (NumberUtil.isLong(source)) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(source)), ZoneId.systemDefault());
            } else {
                return LocalDateTime.parse(source, DatePattern.NORM_DATETIME_FORMATTER);
            }
        }
    }

}
