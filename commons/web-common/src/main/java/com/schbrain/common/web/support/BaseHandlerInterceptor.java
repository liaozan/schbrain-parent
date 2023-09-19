package com.schbrain.common.web.support;

import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liaozan
 * @since 2022/11/11
 */
public class BaseHandlerInterceptor implements AsyncHandlerInterceptor {

    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }
        if (handler instanceof HandlerMethod) {
            return preHandle(request, response, (HandlerMethod) handler);
        }
        return true;
    }

    @Override
    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            postHandle(request, response, (HandlerMethod) handler, modelAndView);
        }
    }

    @Override
    public final void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            afterCompletion(request, response, (HandlerMethod) handler, ex);
        }
    }

    @Override
    public final void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            afterConcurrentHandlingStarted(request, response, (HandlerMethod) handler);
        }
    }

    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        return true;
    }

    protected void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, ModelAndView modelAndView) throws Exception {

    }

    protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception ex) throws Exception {

    }

    protected void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

    }

}
