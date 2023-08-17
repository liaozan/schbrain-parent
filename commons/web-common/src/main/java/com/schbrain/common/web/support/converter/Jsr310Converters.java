package com.schbrain.common.web.support.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

/**
 * @author liaozan
 * @since 2023/8/16
 */
public class Jsr310Converters {

    public static List<Converter<?, ?>> getConverters() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(LongToLocalDateConverter.INSTANCE);
        converters.add(LongToLocalTimeConverter.INSTANCE);
        converters.add(LongToLocalDateTimeConverter.INSTANCE);

        converters.add(StringToLocalDateConverter.INSTANCE);
        converters.add(StringToLocalTimeConverter.INSTANCE);
        converters.add(StringToLocalDateTimeConverter.INSTANCE);

        return converters;
    }

    public enum LongToLocalDateConverter implements Converter<Long, LocalDate> {

        INSTANCE;

        @Override
        public LocalDate convert(Long source) {
            return LongToLocalDateTimeConverter.INSTANCE.convert(source).toLocalDate();
        }
    }

    public enum LongToLocalTimeConverter implements Converter<Long, LocalTime> {

        INSTANCE;

        @Override
        public LocalTime convert(Long source) {
            return LongToLocalDateTimeConverter.INSTANCE.convert(source).toLocalTime();
        }
    }

    public enum LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {

        INSTANCE;

        @Override
        public LocalDateTime convert(Long source) {
            return ofInstant(Instant.ofEpochMilli(source), systemDefault());
        }
    }

    public enum StringToLocalDateConverter implements Converter<String, LocalDate> {

        INSTANCE;

        @Override
        public LocalDate convert(String source) {
            return StringToLocalDateTimeConverter.INSTANCE.convert(source).toLocalDate();
        }
    }

    public enum StringToLocalTimeConverter implements Converter<String, LocalTime> {

        INSTANCE;

        @Override
        public LocalTime convert(String source) {
            return StringToLocalDateTimeConverter.INSTANCE.convert(source).toLocalTime();
        }
    }

    public enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

        INSTANCE;

        @Override
        public LocalDateTime convert(String source) {
            return ofInstant(Instant.ofEpochMilli(Long.parseLong(source)), systemDefault());
        }
    }

}
