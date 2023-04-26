package com.schbrain.framework.autoconfigure.oss.exception;

import com.schbrain.common.exception.BaseException;

/**
 * @author liaozan
 * @since 2021/12/3
 */
public class OssException extends BaseException {

    private static final long serialVersionUID = 7030196267316583562L;

    public OssException(String message) {
        this(message, null);
    }

    public OssException(String message, Throwable cause) {
        super(message, cause);
    }

}
