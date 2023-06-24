package com.schbrain.common.web.servlet;

import com.schbrain.common.util.TraceIdUtils;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * @author liaozan
 * @since 2021/12/8
 */
public class TraceIdInitializeServletListener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        // Make sure the traceId can be cleared
        TraceIdUtils.clear();
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        // Make sure the traceId is initialized
        TraceIdUtils.get();
    }

}