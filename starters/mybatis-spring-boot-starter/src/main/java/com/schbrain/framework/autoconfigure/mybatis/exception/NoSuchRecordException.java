package com.schbrain.framework.autoconfigure.mybatis.exception;

import com.schbrain.common.exception.BaseException;

import java.io.Serializable;

/**
 * @author liaozan
 * @since 2022/04/15
 */
public class NoSuchRecordException extends BaseException {

    private static final long serialVersionUID = -2197824144318250175L;

    public NoSuchRecordException(Class<?> entityClass, Serializable id) {
        super(String.format("No %s entity with id %s exists!", entityClass.getSimpleName(), id));
    }

}