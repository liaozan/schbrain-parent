package com.schbrain.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.schbrain.common.constants.ResponseActionConstants.ALERT;
import static com.schbrain.common.constants.ResponseCodeConstants.SERVER_ERROR;

/**
 * @author liaozan
 * @since 2021/10/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 6235672740816644251L;

    protected final int code;

    protected final int action;

    public BaseException(String message) {
        this(message, null);
    }

    public BaseException(String message, Throwable throwable) {
        this(message, throwable, SERVER_ERROR, ALERT);
    }

    public BaseException(String message, int code, int action) {
        this(message, null, code, action);
    }

    public BaseException(String message, Throwable cause, int code, int action) {
        super(message, cause);
        this.code = code;
        this.action = action;
    }

}