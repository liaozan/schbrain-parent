package com.schbrain.common.web.utils;

import org.springframework.util.Assert;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liaozan
 * @since 2023-05-08
 */
public class ContentCachingServletUtils {

    /**
     * Make request content cacheable to avoid stream closed error after inputStream closed
     */
    public static HttpServletRequest wrapRequestIfRequired(HttpServletRequest request) {
        Assert.notNull(request, "request must not be null");
        if (request instanceof ContentCachingRequestWrapper) {
            return request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    /**
     * Make response content cacheable to avoid stream closed error after outputStream closed
     */
    public static HttpServletResponse wrapResponseIfRequired(HttpServletResponse response) {
        Assert.notNull(response, "response must not be null");
        if (response instanceof ContentCachingResponseWrapper) {
            return response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

}