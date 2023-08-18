package com.schbrain.common.web.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author liaozan
 * @since 2023-05-08
 */
@Slf4j
public class ContentCachingServletUtils {

    /**
     * Make request content cacheable to avoid stream closed error after inputStream closed
     */
    public static ContentCachingRequestWrapper wrapRequestIfRequired(HttpServletRequest request) {
        Assert.notNull(request, "request must not be null");
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    /**
     * Make response content cacheable to avoid stream closed error after outputStream closed
     */
    public static ContentCachingResponseWrapper wrapResponseIfRequired(HttpServletResponse response) {
        Assert.notNull(response, "response must not be null");
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    public static String getRequestBody(HttpServletRequest request, boolean readFromInputStream) {
        ContentCachingRequestWrapper nativeRequest = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (nativeRequest == null) {
            return null;
        }
        Charset charset = Charset.forName(nativeRequest.getCharacterEncoding());
        if (readFromInputStream) {
            try {
                return StreamUtils.copyToString(request.getInputStream(), charset);
            } catch (IOException e) {
                log.warn("Failed to read body content from request inputStream");
                return null;
            }
        }
        return new String(nativeRequest.getContentAsByteArray(), charset);
    }

}
