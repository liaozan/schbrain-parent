package com.schbrain.framework.autoconfigure.mybatis.biz;

import com.schbrain.common.exception.BaseException;
import com.schbrain.framework.autoconfigure.mybatis.annotation.BizId;
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
            Class<?> fieldType = bizIdField.getType();
            Lookup lookup = privateLookupIn(entityClass, lookup());
            this.bizIdFieldGetterMethodHandle = lookup.findGetter(entityClass, bizIdField.getName(), fieldType);
            this.bizIdFieldSetterMethodHandle = lookup.findSetter(entityClass, bizIdField.getName(), fieldType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <V, T> V getValue(T entity) {
        try {
            return (V) bizIdFieldGetterMethodHandle.invoke(entity);
        } catch (Throwable e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

    public <V, T> void setValue(T entity, V value) {
        try {
            bizIdFieldSetterMethodHandle.invoke(entity, value);
        } catch (Throwable e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

}
