package com.schbrain.framework.autoconfigure.mybatis.core;

import com.schbrain.common.exception.BaseException;
import com.schbrain.framework.autoconfigure.mybatis.annotation.BizId;
import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdHelper;
import lombok.Data;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

import static java.lang.invoke.MethodHandles.*;

/**
 * @author liaozan
 * @since 2023-03-23
 */
@Data
public class BizIdColumnField {

    private final BizId annotation;

    private final String columnName;

    private final MethodHandle bizIdFieldGetterMethodHandle;

    private final MethodHandle bizIdFieldSetterMethodHandle;

    public BizIdColumnField(Class<?> entityClass, Field bizIdField) {
        this.annotation = bizIdField.getAnnotation(BizId.class);
        this.columnName = BizIdHelper.getColumnName(entityClass, bizIdField, this.annotation);
        try {
            this.bizIdFieldGetterMethodHandle = privateLookupIn(entityClass, lookup()).findGetter(entityClass, bizIdField.getName(), String.class);
            this.bizIdFieldSetterMethodHandle = privateLookupIn(entityClass, lookup()).findSetter(entityClass, bizIdField.getName(), String.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

    public <T> String getValue(T entity) {
        try {
            return (String) bizIdFieldGetterMethodHandle.invoke(entity);
        } catch (Throwable e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

    public <T> void setValue(T entity, String value) {
        try {
            bizIdFieldSetterMethodHandle.invoke(entity, value);
        } catch (Throwable e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

}