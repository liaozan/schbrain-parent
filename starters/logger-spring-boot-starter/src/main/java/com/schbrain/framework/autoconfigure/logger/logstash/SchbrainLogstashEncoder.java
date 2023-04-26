package com.schbrain.framework.autoconfigure.logger.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.composite.AbstractCompositeJsonFormatter;
import net.logstash.logback.encoder.LogstashEncoder;

/**
 * @author liaozan
 * @since 2022/1/4
 */
public class SchbrainLogstashEncoder extends LogstashEncoder {

    @Override
    protected AbstractCompositeJsonFormatter<ILoggingEvent> createFormatter() {
        return new SchbrainLogstashFormatter(this);
    }

}
