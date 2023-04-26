package com.schbrain.common.util.log;

import com.schbrain.common.util.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticLogUtil {

    public static final Logger log = LoggerFactory.getLogger(StatisticLogUtil.class);

    public static <T extends LogEventAction> void logEvent(LogEvent<T> logEvent) {
        if (null != logEvent) {
            log.info(JacksonUtils.toJsonString(logEvent));
        }
    }

}