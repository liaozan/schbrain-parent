package com.schbrain.common.web.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * @author liaozan
 * @since 2023/8/22
 */
@Slf4j
public class ContentCachingRequest extends HttpServletRequestWrapper {

    private final WrappedByteArrayInputStream inputStream;

    public ContentCachingRequest(HttpServletRequest request) {
        super(request);
        this.inputStream = createWrappedInputStream(request);
    }

    @Override
    public WrappedByteArrayInputStream getInputStream() {
        return inputStream;
    }

    /**
     * Return the cached request content as a String.
     * <p>
     * The Charset used to decode the cached content is the same as returned by getCharacterEncoding.
     */
    public String getContentAsString() {
        return getContentAsString(getCharacterEncoding());
    }

    /**
     * Return the cached request content as a String
     */
    public String getContentAsString(String charset) {
        return inputStream.getContentAsString(charset);
    }

    /**
     * Wrap request inputStream to WrappedByteArrayInputStream
     */
    private WrappedByteArrayInputStream createWrappedInputStream(HttpServletRequest request) {
        try {
            byte[] bytes = StreamUtils.copyToByteArray(request.getInputStream());
            return new WrappedByteArrayInputStream(bytes);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
