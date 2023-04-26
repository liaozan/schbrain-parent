package com.schbrain.common.util;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;

/**
 * @author liaozan
 * @since 2021/10/10
 */
public class TraceIdUtils {

    public static final String TRACE_ID = "traceId";

    private static final boolean skywalkingTracePresent;

    private static final ThreadLocal<String> TRACE_ID_CONTAINER = InheritableThreadLocal.withInitial(TraceIdUtils::create);

    static {
        skywalkingTracePresent = ClassLoaderUtil.isPresent("org.apache.skywalking.apm.toolkit.trace.TraceContext", TraceIdUtils.class.getClassLoader());
    }

    public static String get() {
        return TRACE_ID_CONTAINER.get();
    }

    public static void set(String traceId) {
        if (traceId == null) {
            return;
        }
        MDC.put(TRACE_ID, traceId);
        TRACE_ID_CONTAINER.set(traceId);
    }

    public static void clear() {
        MDC.remove(TRACE_ID);
        TRACE_ID_CONTAINER.remove();
    }

    private static String create() {
        String traceId = MDC.get(TRACE_ID);
        if (traceId == null) {
            if (skywalkingTracePresent) {
                traceId = TraceContext.traceId();
            }
            if (StringUtils.isBlank(traceId) || "N/A".equals(traceId)) {
                traceId = IdUtil.objectId().toUpperCase();
            }
            MDC.put(TRACE_ID, traceId);
        }
        return traceId;
    }

}