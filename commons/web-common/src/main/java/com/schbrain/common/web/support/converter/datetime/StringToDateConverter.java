package com.schbrain.common.web.support.converter.datetime;

import cn.hutool.core.date.DateUtil;

import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author liaozan
 * @since 2023/9/16
 */
class StringToDateConverter extends DateTimeConditionalConverter<Date> {

    static final StringToDateConverter INSTANCE = new StringToDateConverter();

    @Override
    protected Date doConvert(Long source) {
        return DateUtil.date(source);
    }

    @Override
    protected Date doConvert(String source, DateTimeFormatter formatter) {
        return DateUtil.parse(source, formatter);
    }

}
