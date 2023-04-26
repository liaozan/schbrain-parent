package com.schbrain.common.util.support;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author liaozan
 * @since 2021/3/27
 */
public interface ValidateSupport {

    default boolean hasText(String text) {
        return StringUtils.isNotBlank(text);
    }

    default boolean isBlank(String text) {
        return !hasText(text);
    }

    default boolean isNull(Object object) {
        return object == null;
    }

    default boolean isNotNull(Object object) {
        return !isNull(object);
    }

    default boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    default boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    default boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    default boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    default String fixNull(String value) {
        return fixNull(value, "");
    }

    default <T> List<T> fixNull(List<T> value) {
        return fixNull(value, new ArrayList<>());
    }

    default <T> Set<T> fixNull(Set<T> value) {
        return fixNull(value, new HashSet<>());
    }

    default <T> T fixNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

}