package com.schbrain.common.util.support.excel.exception;

import com.schbrain.common.exception.BaseException;

/**
 * @author liaozan
 * @since 2022/1/6
 */
public class ExcelException extends BaseException {

    private static final long serialVersionUID = -2338463360273587530L;

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }

}
