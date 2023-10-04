package com.schbrain.common.web.support.converter.datetime;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author liaozan
 * @since 2023/9/16
 */
class StringToDateConditionalConverter extends DateTimeConditionalConverter<Date> {

    static final StringToDateConditionalConverter INSTANCE = new StringToDateConditionalConverter();

    @Override
    protected String defaultPattern() {
        return DatePattern.NORM_DATETIME_PATTERN;
    }

    @Override
    protected Date doConvert(Long source) {
        return DateUtil.date(source);
    }

    @Override
    protected Date doConvert(String source, DateTimeFormatter formatter) {
        return DateUtil.parse(source, formatter);
    }

}
