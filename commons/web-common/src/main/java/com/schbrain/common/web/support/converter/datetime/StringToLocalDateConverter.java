package com.schbrain.common.web.support.converter.datetime;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author liaozan
 * @since 2023/9/16
 */
class StringToLocalDateConverter extends DateTimeConditionalConverter<LocalDate> {

    static final StringToLocalDateConverter INSTANCE = new StringToLocalDateConverter();

    @Override
    protected String defaultPattern() {
        return DatePattern.NORM_DATE_PATTERN;
    }

    @Override
    protected LocalDate doConvert(Long source) {
        return LocalDateTimeUtil.of(source).toLocalDate();
    }

    @Override
    protected LocalDate doConvert(String source, DateTimeFormatter formatter) {
        return LocalDate.parse(source, formatter);
    }

}
