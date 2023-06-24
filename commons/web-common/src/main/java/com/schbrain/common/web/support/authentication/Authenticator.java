package com.schbrain.common.web.support.authentication;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liaozan
 * @since 2022/11/11
 */
public interface Authenticator {

    /**
     * 校验当前请求是否合法
     */
    boolean validate(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler);

    /**
     * 请求完成后的回调
     */
    void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception exception);

}