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

    private WrappedByteArrayInputStream inputStream;

    public ContentCachingRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public WrappedByteArrayInputStream getInputStream() throws IOException {
        if (inputStream == null) {
            byte[] bytes = StreamUtils.copyToByteArray(super.getInputStream());
            this.inputStream = new WrappedByteArrayInputStream(bytes);
        }
        return inputStream;
    }

    /**
     * Return the cached request content as a String. The Charset used to decode the cached content is the same as returned by getCharacterEncoding.
     */
    public String getContentAsString() {
        return getContentAsString(getCharacterEncoding());
    }

    /**
     * Return the cached request content as a String
     */
    public String getContentAsString(String charset) {
        try {
            return getInputStream().getContentAsString(charset);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
