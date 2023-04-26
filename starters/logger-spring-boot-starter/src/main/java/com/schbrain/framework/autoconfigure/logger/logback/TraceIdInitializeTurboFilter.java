package com.schbrain.framework.autoconfigure.logger.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import com.schbrain.common.util.TraceIdUtils;
import org.slf4j.Marker;

/**
 * @author liaozan
 * @since 2023-04-08
 */
public class TraceIdInitializeTurboFilter extends TurboFilter {

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable throwable) {
        // Make sure the traceId is initialized
        TraceIdUtils.get();
        return FilterReply.NEUTRAL;
    }

}