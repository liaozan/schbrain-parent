package com.schbrain.common.exception;

import static com.schbrain.common.constants.ResponseActionConstants.ALERT;
import static com.schbrain.common.constants.ResponseCodeConstants.PARAM_INVALID;

/**
 * @author liwu
 * @since 2019/3/29
 */
public class ParamInvalidException extends BaseException {

    private static final long serialVersionUID = -4015658097738003486L;

    public ParamInvalidException(String message) {
        super(message, PARAM_INVALID, ALERT);
    }

}