package com.schbrain.common.web.result;

import com.schbrain.common.constants.ResponseActionConstants;
import com.schbrain.common.constants.ResponseCodeConstants;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.TraceIdUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liaozan
 * @since 2021/10/15
 */
@Data
public class ResponseDTO<T> implements Serializable {

    private static final long serialVersionUID = 8559474982311419998L;

    private int code;
    private int action;
    private String message;
    private T data;
    private String uuid = TraceIdUtils.get();

    public static <T> ResponseDTO<T> success() {
        return success(null);
    }

    public static <T> ResponseDTO<T> success(T data) {
        ResponseDTO<T> result = new ResponseDTO<>();
        result.setMessage(null);
        result.setCode(ResponseCodeConstants.SUCCESS);
        result.setAction(ResponseActionConstants.NO_ACTION);
        result.setData(data);
        return result;
    }

    public static <T> ResponseDTO<T> error(String message) {
        return error(message, ResponseCodeConstants.SERVER_ERROR);
    }

    public static <T> ResponseDTO<T> error(String message, int code) {
        return error(message, code, ResponseActionConstants.ALERT);
    }

    public static <T> ResponseDTO<T> error(BaseException exception) {
        return error(exception.getMessage(), exception.getCode(), exception.getAction());
    }

    public static <T> ResponseDTO<T> error(String message, int code, int action) {
        ResponseDTO<T> result = new ResponseDTO<>();
        result.setMessage(message);
        result.setCode(code);
        result.setAction(action);
        return result;
    }

}