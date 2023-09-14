package com.schbrain.common.web.utils;

import com.schbrain.common.util.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;

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
        if (request instanceof ContentCachingRequestWrapper) {
            return request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    /**
     * Get request body content
     */
    @Nullable
    public static String getRequestBody(HttpServletRequest request) {
        return getRequestBody(request, Charset.forName(request.getCharacterEncoding()));
    }

    /**
     * Get request body content
     */
    @Nullable
    public static String getRequestBody(HttpServletRequest request, Charset characterEncoding) {
        ContentCachingRequestWrapper requestToUse = getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (requestToUse == null) {
            return null;
        }
        byte[] content = requestToUse.getContentAsByteArray();
        return new String(content, characterEncoding);
    }

}
