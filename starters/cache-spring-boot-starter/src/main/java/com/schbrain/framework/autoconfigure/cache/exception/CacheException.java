package com.schbrain.framework.autoconfigure.cache.exception;

/**
 * @author zhuyf
 * @since 2022/7/25
 */
public class CacheException extends RuntimeException {

    private static final long serialVersionUID = -1948413195425101948L;

    public CacheException(String message) {
        super(message);
    }

}