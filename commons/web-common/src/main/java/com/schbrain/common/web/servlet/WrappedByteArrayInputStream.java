package com.schbrain.common.web.servlet;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

/**
 * @author liaozan
 * @since 2023/8/22
 */
public class WrappedByteArrayInputStream extends ServletInputStream {

    private final ByteArrayInputStreamWrapper delegate;

    public WrappedByteArrayInputStream(byte[] bytes) {
        this.delegate = new ByteArrayInputStreamWrapper(bytes);
    }

    @Override
    public boolean isFinished() {
        return delegate.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener ignore) {

    }

    @Override
    public int read() {
        return delegate.read();
    }

    /**
     * Return the cached request content as a String
     */
    public String getContentAsString(String charset) {
        return new String(delegate.getBytes(), Charset.forName(charset));
    }

    /**
     * Simple {@link ByteArrayInputStream} wrapper that exposes the underlying byte array.
     */
    private static class ByteArrayInputStreamWrapper extends ByteArrayInputStream {

        public ByteArrayInputStreamWrapper(byte[] bytes) {
            super(bytes);
        }

        public byte[] getBytes() {
            return buf;
        }

    }

}
