package com.schbrain.common.util.exception;

import com.schbrain.common.constants.ResponseActionConstants;
import com.schbrain.common.constants.ResponseCodeConstants;
import com.schbrain.common.exception.BaseException;

/**
 * @author liaozan
 * @since 2023-05-06
 */
public class JSONException extends BaseException {

    private static final long serialVersionUID = 1656914307906296812L;

    public JSONException(String message, Throwable cause) {
        super(message, cause, ResponseCodeConstants.SERVER_ERROR, ResponseActionConstants.ALERT);
    }

}