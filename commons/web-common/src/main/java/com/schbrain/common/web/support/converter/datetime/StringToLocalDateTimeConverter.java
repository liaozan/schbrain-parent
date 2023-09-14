package com.schbrain.common.web.support.converter.datetime;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liaozan
 * @since 2023/9/16
 */
class StringToLocalDateTimeConverter extends DateTimeConditionalConverter<LocalDateTime> {

    static final StringToLocalDateTimeConverter INSTANCE = new StringToLocalDateTimeConverter();

    @Override
    protected LocalDateTime doConvert(Long source) {
        return LocalDateTimeUtil.of(source);
    }

    @Override
    protected LocalDateTime doConvert(String source, DateTimeFormatter formatter) {
        return LocalDateTime.parse(source, formatter);
    }

}
