package com.schbrain.common.web.support.signature;

import com.schbrain.common.exception.BaseException;

import static com.schbrain.common.constants.ResponseActionConstants.ALERT;
import static com.schbrain.common.constants.ResponseCodeConstants.PARAM_INVALID;

public class SignatureValidationException extends BaseException {

    private static final long serialVersionUID = 7564001466173362458L;

    private static final String DEFAULT_ERR_MSG = "签名验证异常";

    public SignatureValidationException() {
        this(DEFAULT_ERR_MSG);
    }

    public SignatureValidationException(String message) {
        super(message, PARAM_INVALID, ALERT);
    }

}
