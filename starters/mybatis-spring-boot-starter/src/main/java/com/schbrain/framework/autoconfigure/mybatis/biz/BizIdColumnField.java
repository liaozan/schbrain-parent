package com.schbrain.framework.autoconfigure.mybatis.biz;

import com.schbrain.common.exception.BaseException;
import com.schbrain.framework.autoconfigure.mybatis.annotation.BizId;
import lombok.Getter;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

import static java.lang.invoke.MethodHandles.Lookup;
import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodHandles.privateLookupIn;

/**
 * @author liaozan
 * @since 2023-03-23
 */
@Getter
public class BizIdColumnField {

    private final BizId annotation;

    private final String columnName;

    private final Class<?> fieldType;

    private final MethodHandle getter;

    private final MethodHandle setter;

    public BizIdColumnField(Class<?> entityClass, Field bizIdField) {
        this.annotation = bizIdField.getAnnotation(BizId.class);
        this.columnName = BizIdHelper.getColumnName(entityClass, bizIdField, this.annotation);
        this.fieldType = bizIdField.getType();
        try {
            Lookup lookup = privateLookupIn(entityClass, lookup());
            this.getter = lookup.unreflectGetter(bizIdField);
            this.setter = lookup.unreflectSetter(bizIdField);
        } catch (IllegalAccessException e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <V, T> V getValue(T entity) {
        try {
            return (V) getter.invoke(entity);
        } catch (Throwable e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

    public <V, T> void setValue(T entity, V value) {
        try {
            setter.invoke(entity, value);
        } catch (Throwable e) {
            throw new BaseException(e.getMessage(), e);
        }
    }

}
