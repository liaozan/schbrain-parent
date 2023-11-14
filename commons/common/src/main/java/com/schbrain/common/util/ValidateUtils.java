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

    public static void isTrue(boolean expression) {
        isTrue(expression, "The validated object is false");
    }

    public static void isTrue(boolean expression, String message) {
        isTrue(expression, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isTrue(boolean expression, Supplier<T> errorSupplier) {
        if (!expression) {
            throw errorSupplier.get();
        }
    }

    public static void isFalse(boolean expression) {
        isFalse(expression, "The validated object is true");
    }

    public static void isFalse(boolean expression, String message) {
        isFalse(expression, () -> new ParamInvalidException(message));
    }

    public static <E extends BaseException> void isFalse(boolean expression, Supplier<E> errorSupplier) {
        if (expression) {
            throw errorSupplier.get();
        }
    }

    public static <T> T notNull(T object) {
        return notNull(object, "The validated object is null");
    }

    public static <T> T notNull(T object, String message) {
        return notNull(object, () -> new ParamInvalidException(message));
    }

    public static <T, E extends BaseException> T notNull(T object, Supplier<E> errorSupplier) {
        if (object == null) {
            throw errorSupplier.get();
        }
        return object;
    }

    public static void isNull(Object object) {
        isNull(object, "The validated object is not null");
    }

    public static void isNull(Object object, String message) {
        isNull(object, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isNull(Object object, Supplier<T> errorSupplier) {
        if (object != null) {
            throw errorSupplier.get();
        }
    }

    public static String notEmpty(String value) {
        return notEmpty(value, "The validated string is empty");
    }

    public static String notEmpty(String value, String message) {
        return notEmpty(value, () -> new ParamInvalidException(message));
    }

    public static <E extends BaseException> String notEmpty(String value, Supplier<E> errorSupplier) {
        if (value == null || value.isBlank()) {
            throw errorSupplier.get();
        }
        return value;
    }

    public static void isEmpty(String value) {
        isEmpty(value, "The validated string is not empty");
    }

    public static void isEmpty(String value, String message) {
        isEmpty(value, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isEmpty(String value, Supplier<T> errorSupplier) {
        if (value != null && !value.isEmpty()) {
            throw errorSupplier.get();
        }
    }

    public static <T> T[] notEmpty(T[] array) {
        return notEmpty(array, "The validated array is empty");
    }

    public static <T> T[] notEmpty(T[] array, String message) {
        return notEmpty(array, () -> new ParamInvalidException(message));
    }

    public static <T, E extends BaseException> T[] notEmpty(T[] array, Supplier<E> errorSupplier) {
        if (array == null || array.length == 0) {
            throw errorSupplier.get();
        }
        return array;
    }

    public static void isEmpty(Object[] array) {
        isEmpty(array, "The validated array is not empty");
    }

    public static void isEmpty(Object[] array, String message) {
        isEmpty(array, () -> new ParamInvalidException(message));
    }

    public static <E extends BaseException> void isEmpty(Object[] array, Supplier<E> errorSupplier) {
        if (array != null && array.length != 0) {
            throw errorSupplier.get();
        }
    }

    public static <T, C extends Collection<T>> C notEmpty(C collection) {
        return notEmpty(collection, "The validated collection is empty");
    }

    public static <T, C extends Collection<T>> C notEmpty(C collection, String message) {
        return notEmpty(collection, () -> new ParamInvalidException(message));
    }

    public static <T, C extends Collection<T>, E extends BaseException> C notEmpty(C collection, Supplier<E> errorSupplier) {
        if (collection == null || collection.isEmpty()) {
            throw errorSupplier.get();
        }
        return collection;
    }

    public static void isEmpty(Collection<?> collection) {
        isEmpty(collection, "The validated collection is not empty");
    }

    public static void isEmpty(Collection<?> collection, String message) {
        isEmpty(collection, () -> new ParamInvalidException(message));
    }

    public static <T extends BaseException> void isEmpty(Collection<?> collection, Supplier<T> errorSupplier) {
        if (collection != null && !collection.isEmpty()) {
            throw errorSupplier.get();
        }
    }

    public static <K, V> Map<K, V> notEmpty(Map<K, V> map) {
        return notEmpty(map, "The validated map is empty");
    }

    public static <K, V> Map<K, V> notEmpty(Map<K, V> map, String message) {
        return notEmpty(map, () -> new ParamInvalidException(message));
    }

    public static <K, V, E extends BaseException> Map<K, V> notEmpty(Map<K, V> map, Supplier<E> errorSupplier) {
        if (map == null || map.isEmpty()) {
            throw errorSupplier.get();
        }
        return map;
    }

    public static void isEmpty(Map<?, ?> map) {
        isEmpty(map, "The validated map is not empty");
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
