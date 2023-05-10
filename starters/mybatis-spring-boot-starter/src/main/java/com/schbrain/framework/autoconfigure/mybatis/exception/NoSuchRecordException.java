package com.schbrain.framework.autoconfigure.mybatis.exception;

import com.schbrain.common.exception.BaseException;

/**
 * @author liaozan
 * @since 2022/04/15
 */
public class NoSuchRecordException extends BaseException {

    private static final long serialVersionUID = -2197824144318250175L;

    public NoSuchRecordException(Class<?> entityClass) {
        super(String.format("No such %s entity exists!", entityClass.getSimpleName()));
    }

}