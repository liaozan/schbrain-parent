package com.schbrain.framework.autoconfigure.logger.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;

import java.io.IOException;

/**
 * @author liaozan
 * @since 2023/8/31
 */
public class NodeIpJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

    private static final String FIELD_NODE_IP = "nodeIp";

    /**
     * Retrieves the node ip from the k8s environment variable NODE_IP.
     */
    private static final String NODE_IP = System.getenv("NODE_IP");

    public NodeIpJsonProvider() {
        setFieldName(FIELD_NODE_IP);
    }

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent iLoggingEvent) throws IOException {
        JsonWritingUtils.writeStringField(generator, getFieldName(), NODE_IP);
    }

}
