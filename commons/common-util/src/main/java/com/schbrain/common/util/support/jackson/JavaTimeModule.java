package com.schbrain.common.util.support.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import com.schbrain.common.constants.DateTimeFormatters;

import java.time.*;

/**
 * @author liaozan
 * @since 2022/8/12
 */
public class JavaTimeModule extends SimpleModule {

    private static final long serialVersionUID = -1848725752934764693L;

    public JavaTimeModule() {
        this.setup();
    }

    protected void setup() {
        this.addSerializer(YearMonth.class, new YearMonthSerializer(DateTimeFormatters.YEAR_MONTH));
        this.addSerializer(MonthDay.class, new MonthDaySerializer(DateTimeFormatters.MONTH_DATE));
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatters.DATE));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatters.TIME));
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatters.DATE_TIME));

        this.addDeserializer(YearMonth.class, new YearMonthDeserializer(DateTimeFormatters.YEAR_MONTH));
        this.addDeserializer(MonthDay.class, new MonthDayDeserializer(DateTimeFormatters.MONTH_DATE));
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatters.DATE));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatters.TIME));
        this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatters.DATE_TIME));
    }

}