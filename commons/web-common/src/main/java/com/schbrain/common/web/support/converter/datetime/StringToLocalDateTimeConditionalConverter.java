package com.schbrain.common.web.support.converter.datetime;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liaozan
 * @since 2023/9/16
 */
class StringToLocalDateTimeConditionalConverter extends DateTimeConditionalConverter<LocalDateTime> {

    static final StringToLocalDateTimeConditionalConverter INSTANCE = new StringToLocalDateTimeConditionalConverter();

    @Override
    protected LocalDateTime doConvert(Long source) {
        return LocalDateTimeUtil.of(source);
    }

    @Override
    protected LocalDateTime doConvert(String source, DateTimeFormatter formatter) {
        return LocalDateTime.parse(source, formatter);
    }

}
