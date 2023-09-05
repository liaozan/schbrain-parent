package com.schbrain.common.web.utils;

import com.schbrain.common.util.ValidateUtils;
import com.schbrain.common.web.servlet.ContentCachingRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
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
    public static HttpServletRequest wrapIfRequired(HttpServletRequest request) {
        ValidateUtils.notNull(request, "request must not be null");
        if (request instanceof ContentCachingRequest || !isJsonPostRequest(request)) {
            return request;
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
            return null;
        }
        return requestToUse.getContentAsString(characterEncoding);
    }

    private static boolean isJsonPostRequest(HttpServletRequest request) {
        return POST.matches(request.getMethod()) && StringUtils.contains(request.getContentType(), APPLICATION_JSON_VALUE);
    }

}
