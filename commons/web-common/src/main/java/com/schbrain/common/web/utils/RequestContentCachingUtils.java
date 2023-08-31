package com.schbrain.common.web.utils;

import com.schbrain.common.util.ValidateUtils;
import com.schbrain.common.web.servlet.ContentCachingRequest;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.util.WebUtils.getNativeRequest;

/**
 * @author liaozan
 * @since 2023-05-08
 */
@Slf4j
public class RequestContentCachingUtils {

    /**
     * Make request content cacheable to avoid stream closed error after inputStream closed
     */
    public static ContentCachingRequest wrapIfRequired(HttpServletRequest request) {
        ValidateUtils.notNull(request, "request must not be null");
        if (request instanceof ContentCachingRequest) {
            return (ContentCachingRequest) request;
        } else {
            return new ContentCachingRequest(request);
        }
    }

    /**
     * Get request body content
     */
    @Nullable
    public static String getRequestBody(HttpServletRequest request) {
        return getRequestBody(request, request.getCharacterEncoding());
    }

    /**
     * Get request body content
     */
    @Nullable
    public static String getRequestBody(HttpServletRequest request, String characterEncoding) {
        ContentCachingRequest requestToUse = getNativeRequest(request, ContentCachingRequest.class);
        if (requestToUse == null) {
            log.warn("request is not an instance of {}", ContentCachingRequest.class.getSimpleName());
            return null;
        }
        return requestToUse.getContentAsString(characterEncoding);
    }

}
