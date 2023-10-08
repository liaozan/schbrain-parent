package com.schbrain.common.util.support.jackson;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;

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
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DatePattern.NORM_DATE_FORMATTER));
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DatePattern.NORM_TIME_FORMATTER));
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DatePattern.NORM_DATETIME_FORMATTER));

        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DatePattern.NORM_DATE_FORMATTER));
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DatePattern.NORM_TIME_FORMATTER));
        this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DatePattern.NORM_DATETIME_FORMATTER));
    }

}
