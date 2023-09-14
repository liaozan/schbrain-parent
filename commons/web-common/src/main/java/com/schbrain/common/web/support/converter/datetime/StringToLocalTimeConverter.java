package com.schbrain.common.web.support.converter.datetime;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liaozan
 * @since 2023/9/16
 */
class StringToLocalTimeConverter extends DateTimeConditionalConverter<LocalTime> {

    static final StringToLocalTimeConverter INSTANCE = new StringToLocalTimeConverter();

    @Override
    protected String defaultPattern() {
        return DatePattern.NORM_TIME_PATTERN;
    }

    @Override
    protected LocalTime doConvert(Long source) {
        return LocalDateTimeUtil.of(source).toLocalTime();
    }

    @Override
    protected LocalTime doConvert(String source, DateTimeFormatter formatter) {
        return LocalTime.parse(source, formatter);
    }

}
