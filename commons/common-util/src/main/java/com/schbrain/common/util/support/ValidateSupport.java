package com.schbrain.common.util.support;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author liaozan
 * @since 2021/3/27
 */
public interface ValidateSupport {

    default boolean isEmpty(String text) {
        return StringUtils.isEmpty(text);
    }

    default boolean isNotEmpty(String text) {
        return StringUtils.isNotEmpty(text);
    }

    default boolean isBlank(String text) {
        return StringUtils.isBlank(text);
    }

    default boolean isNotBlank(String text) {
        return StringUtils.isNotBlank(text);
    }

    default boolean isNull(Object object) {
        return Objects.isNull(object);
    }

    default boolean isNotNull(Object object) {
        return Objects.nonNull(object);
    }

    default boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    default boolean isNotEmpty(Collection<?> collection) {
        return CollectionUtils.isNotEmpty(collection);
    }

    default boolean isEmpty(Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    default boolean isNotEmpty(Map<?, ?> map) {
        return MapUtils.isNotEmpty(map);
    }

    default String fixNull(String value) {
        return fixNull(value, "");
    }

    default <T> List<T> fixNull(List<T> value) {
        return fixNull(value, ArrayList::new);
    }

    default <T> Set<T> fixNull(Set<T> value) {
        return fixNull(value, HashSet::new);
    }

    default <K, V> Map<K, V> fixNull(Map<K, V> value) {
        return fixNull(value, HashMap::new);
    }

    default <T> T fixNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    default <T> T fixNull(T value, Supplier<T> defaultValueSupplier) {
        return value != null ? value : defaultValueSupplier.get();
    }

}