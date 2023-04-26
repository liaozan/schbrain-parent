package com.schbrain.common.util;

import com.schbrain.common.exception.ParamInvalidException;

import java.util.*;

public class ValidateUtils {

    /**
     * Constructor. This class should not normally be instantiated.
     */
    private ValidateUtils() {
    }

    public static void isTrue(boolean expression) {
        isTrue(expression, "操作有误");
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new ParamInvalidException(message);
        }
    }

    public static void isFalse(boolean expression) {
        isFalse(expression, "操作有误");
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new ParamInvalidException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "The validated object is null");
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new ParamInvalidException(message);
        }
    }

    public static void isNull(Object object) {
        isNull(object, "The validated object is not null");
    }

    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new ParamInvalidException(message);
        }
    }

    public static void notEmpty(String value) {
        notEmpty(value, "The validated string is empty");
    }

    public static void notEmpty(String value, String message) {
        if (null == value || value.isBlank()) {
            throw new ParamInvalidException(message);
        }
    }

    public static void isEmpty(String value) {
        isEmpty(value, "The validated string is not empty");
    }

    public static void isEmpty(String value, String message) {
        if (value != null && !value.isEmpty()) {
            throw new ParamInvalidException(message);
        }
    }

    public static void notEmpty(Object[] array) {
        notEmpty(array, "The validated array is empty");
    }

    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new ParamInvalidException(message);
        }
    }

    public static void isEmpty(Object[] array) {
        isEmpty(array, "The validated array is not empty");
    }

    public static void isEmpty(Object[] array, String message) {
        if (array != null && array.length != 0) {
            throw new ParamInvalidException(message);
        }
    }

    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, "The validated collection is empty");
    }

    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ParamInvalidException(message);
        }
    }

    public static void isEmpty(Collection<?> collection) {
        isEmpty(collection, "The validated collection is not empty");
    }

    public static void isEmpty(Collection<?> collection, String message) {
        if (collection != null && !collection.isEmpty()) {
            throw new ParamInvalidException(message);
        }
    }

    public static void notEmpty(Map<?, ?> map) {
        notEmpty(map, "The validated map is empty");
    }

    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new ParamInvalidException(message);
        }
    }

    public static void isEmpty(Map<?, ?> map) {
        isEmpty(map, "The validated map is not empty");
    }

    public static void isEmpty(Map<?, ?> map, String message) {
        if (map != null && !map.isEmpty()) {
            throw new ParamInvalidException(message);
        }
    }

    public static void noNullElements(Object[] array) {
        notNull(array);
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new ParamInvalidException("The validated array contains null element at index: " + i);
            }
        }
    }

    public static void noNullElements(Object[] array, String message) {
        notNull(array);
        for (Object item : array) {
            if (item == null) {
                throw new ParamInvalidException(message);
            }
        }
    }

    public static void noNullElements(Collection<?> collection, String message) {
        notNull(collection);
        for (Object item : collection) {
            if (item == null) {
                throw new ParamInvalidException(message);
            }
        }
    }

    public static void noNullElements(Collection<?> collection) {
        notNull(collection);
        int i = 0;
        for (Iterator<?> it = collection.iterator(); it.hasNext(); i++) {
            if (it.next() == null) {
                throw new ParamInvalidException("The validated collection contains null element at index: " + i);
            }
        }
    }

    public static void allElementsOfType(Collection<?> collection, Class<?> clazz, String message) {
        notNull(collection);
        notNull(clazz);
        for (Object item : collection) {
            if (!clazz.isInstance(item)) {
                throw new ParamInvalidException(message);
            }
        }
    }

    public static void allElementsOfType(Collection<?> collection, Class<?> clazz) {
        notNull(collection);
        notNull(clazz);
        int i = 0;
        for (Iterator<?> it = collection.iterator(); it.hasNext(); i++) {
            if (!clazz.isInstance(it.next())) {
                throw new ParamInvalidException("The validated collection contains an element not of type " + clazz.getName() + " at index: " + i);
            }
        }
    }

}