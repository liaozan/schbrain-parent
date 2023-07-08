package com.schbrain.framework.autoconfigure.logger.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liaozan
 * @since 2022/1/11
 */
public class EventDateStringValueJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

    private static final String FIELD_EVENT_DATE = "eventDate";

    private static final DateTimeFormatter DATE_WITH_DOT = DatePattern.createFormatter("yyyy.MM.dd");

    public EventDateStringValueJsonProvider() {
        setFieldName(FIELD_EVENT_DATE);
    }

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
        JsonWritingUtils.writeStringField(generator, FIELD_EVENT_DATE, getEventDate(event));
    }

    private String getEventDate(ILoggingEvent event) {
        LocalDateTime eventTime = LocalDateTimeUtil.of(event.getTimeStamp());
        return DATE_WITH_DOT.format(eventTime);
    }

}
