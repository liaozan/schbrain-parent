package com.schbrain.common.web.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author liaozan
 * @since 2023/8/9
 */
public class LongToStringSerializer extends StdSerializer<Long> {

    public static final LongToStringSerializer instance = new LongToStringSerializer();

    private static final double FRONT_MAX_VALUE = Math.pow(2, 53);
    private static final long serialVersionUID = -1872783127429540811L;

    protected LongToStringSerializer() {
        super(Long.class);
    }

    @Override
    public void serialize(Long value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        if (value.doubleValue() > FRONT_MAX_VALUE) {
            generator.writeString(value.toString());
        } else {
            generator.writeNumber(value);
        }
    }

}
