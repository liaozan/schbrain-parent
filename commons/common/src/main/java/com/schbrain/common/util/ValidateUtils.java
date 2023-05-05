package com.schbrain.common.util;

import com.schbrain.common.exception.BaseException;
import com.schbrain.common.exception.ParamInvalidException;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class ValidateUtils {

    /**
     * Constructor. This class should not normally be instantiated.
     */
    private ValidateUtils() {
    }

    public static void isTrue(boolean expression, String message) {
        isTrue(expression, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isTrue(boolean expression, Supplier<T> errorSupplier) {
        if (!expression) {
            throw errorSupplier.get();
        }
    }

    public static void isFalse(boolean expression, String message) {
        isFalse(expression, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isFalse(boolean expression, Supplier<T> errorSupplier) {
        if (expression) {
            throw errorSupplier.get();
        }
    }

    public static void notNull(Object object, String message) {
        notNull(object, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void notNull(Object object, Supplier<T> errorSupplier) {
        if (object == null) {
            throw errorSupplier.get();
        }
    }

    public static void isNull(Object object, String message) {
        isNull(object, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isNull(Object object, Supplier<T> errorSupplier) {
        if (object != null) {
            throw errorSupplier.get();
        }
    }

    public static void notEmpty(String value, String message) {
        notEmpty(value, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void notEmpty(String value, Supplier<T> errorSupplier) {
        if (value == null || value.isBlank()) {
            throw errorSupplier.get();
        }
    }

    public static void isEmpty(String value, String message) {
        isEmpty(value, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isEmpty(String value, Supplier<T> errorSupplier) {
        if (value != null && !value.isEmpty()) {
            throw errorSupplier.get();
        }
    }

    public static void notEmpty(Object[] array, String message) {
        notEmpty(array, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void notEmpty(Object[] array, Supplier<T> errorSupplier) {
        if (array == null || array.length == 0) {
            throw errorSupplier.get();
        }
    }

    public static void isEmpty(Object[] array, String message) {
        isEmpty(array, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isEmpty(Object[] array, Supplier<T> errorSupplier) {
        if (array != null && array.length != 0) {
            throw errorSupplier.get();
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        notEmpty(collection, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void notEmpty(Collection<?> collection, Supplier<T> errorSupplier) {
        if (collection == null || collection.isEmpty()) {
            throw errorSupplier.get();
        }
    }

    public static void isEmpty(Collection<?> collection, String message) {
        isEmpty(collection, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isEmpty(Collection<?> collection, Supplier<T> errorSupplier) {
        if (collection != null && !collection.isEmpty()) {
            throw errorSupplier.get();
        }
    }

    public static void notEmpty(Map<?, ?> map, String message) {
        notEmpty(map, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void notEmpty(Map<?, ?> map, Supplier<T> errorSupplier) {
        if (map == null || map.isEmpty()) {
            throw errorSupplier.get();
        }
    }

    public static void isEmpty(Map<?, ?> map, String message) {
        isEmpty(map, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isEmpty(Map<?, ?> map, Supplier<T> errorSupplier) {
        if (map != null && !map.isEmpty()) {
            throw errorSupplier.get();
        }
    }

}