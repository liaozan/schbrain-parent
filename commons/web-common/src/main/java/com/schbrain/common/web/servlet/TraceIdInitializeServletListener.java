package com.schbrain.common.web.servlet;

import com.schbrain.common.util.TraceIdUtils;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.ServletRequestEvent;

/**
 * @author liaozan
 * @since 2021/12/8
 */
public class TraceIdInitializeServletListener extends RequestContextListener {

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        super.requestInitialized(event);
        // Make sure the traceId is initialized
        TraceIdUtils.get();
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        super.requestDestroyed(event);
        // Make sure the traceId can be cleared
        TraceIdUtils.clear();
    }

}