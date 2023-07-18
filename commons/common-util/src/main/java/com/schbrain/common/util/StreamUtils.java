package com.schbrain.common.util;

import cn.hutool.core.text.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;

/**
 * @author liaozan
 * @since 2022/8/21
 */
@Slf4j
public class StreamUtils {

    public static <T> List<T> filterToList(Iterable<T> data, Predicate<T> predicate) {
        return filterToList(data, predicate, Function.identity());
    }

    public static <T, V> List<V> filterToList(Iterable<T> data, Predicate<T> predicate, Function<T, V> mapper) {
        return filter(data, predicate, mapper, Collectors.toList());
    }

    public static <T> Set<T> filterToSet(Iterable<T> data, Predicate<T> predicate) {
        return filterToSet(data, predicate, Function.identity());
    }

    public static <T, V> Set<V> filterToSet(Iterable<T> data, Predicate<T> predicate, Function<T, V> mapper) {
        return filter(data, predicate, mapper, Collectors.toSet());
    }

    public static <T, E, V extends Iterable<E>> V filter(Iterable<T> data, Predicate<T> predicate, Function<T, E> mapper, Collector<E, ?, V> collector) {
        return from(data).filter(predicate).map(mapper).collect(collector);
    }

    public static <T, E> List<E> toList(Iterable<T> data, Function<T, E> mapper) {
        return toList(data, mapper, false);
    }

    public static <T, E> List<E> toList(Iterable<T> data, Function<T, E> mapper, boolean distinct) {
        return toList(data, mapper, distinct, false);
    }

    public static <T, E> List<E> toList(Iterable<T> data, Function<T, E> mapper, boolean distinct, boolean discardNull) {
        return extract(data, mapper, distinct, discardNull, Collectors.toList());
    }

    public static <T, E> Set<E> toSet(Iterable<T> data, Function<T, E> mapper) {
        return toSet(data, mapper, false);
    }

    public static <T, E> Set<E> toSet(Iterable<T> data, Function<T, E> mapper, boolean discardNull) {
        return extract(data, mapper, discardNull, false, Collectors.toSet());
    }

    public static <T, E, R> R extract(Iterable<T> data, Function<T, E> mapper, boolean distinct, boolean discardNull, Collector<E, ?, R> collector) {
        Predicate<E> predicate = null;
        if (discardNull) {
            predicate = Objects::nonNull;
        }
        return extract(data, mapper, predicate, distinct, collector);
    }

    public static <T, E, R> R extract(Iterable<T> data, Function<T, E> mapper, Predicate<E> predicate, boolean distinct, Collector<E, ?, R> collector) {
        Stream<E> stream = from(data).map(mapper);
        if (distinct) {
            stream = stream.distinct();
        }
        if (predicate != null) {
            stream = stream.filter(predicate);
        }
        return stream.collect(collector);
    }

    public static <K, V> Map<K, V> toMap(Iterable<V> data, Function<V, K> keyMapper) {
        return toMap(data, keyMapper, false);
    }

    public static <K, V> Map<K, V> toMap(Iterable<V> data, Function<V, K> keyMapper, boolean ordered) {
        return toMap(data, keyMapper, Function.identity(), ordered);
    }

    public static <K, T, V> Map<K, V> toMap(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return toMap(data, keyMapper, valueMapper, false);
    }

    public static <K, T, V> Map<K, V> toMap(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper, boolean ordered) {
        Supplier<Map<K, V>> mapFactory = HashMap::new;
        if (ordered) {
            mapFactory = LinkedHashMap::new;
        }
        return toMap(data, keyMapper, valueMapper, mapFactory);
    }

    public static <K, T, V, M extends Map<K, V>> Map<K, V> toMap(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<M> mapFactory) {
        return from(data).collect(Collectors.toMap(keyMapper, valueMapper, (oldValue, newValue) -> oldValue, mapFactory));
    }

    public static <K, V> Map<K, List<V>> groupBy(Iterable<V> data, Function<V, K> mapper) {
        return groupBy(data, mapper, false);
    }

    public static <K, T> Map<K, List<T>> groupBy(Iterable<T> data, Function<T, K> keyMapper, boolean ignoreNullKey) {
        return groupBy(data, keyMapper, Collectors.toList(), ignoreNullKey);
    }

    public static <K, T, V> Map<K, V> groupBy(Iterable<T> data, Function<T, K> mapper, Collector<T, ?, V> collectors) {
        return groupBy(data, mapper, collectors, false);
    }

    public static <K, T, V> Map<K, V> groupBy(Iterable<T> data, Function<T, K> mapper, Collector<T, ?, V> collectors, boolean discardNullKey) {
        return groupBy(data, mapper, Function.identity(), collectors, discardNullKey);
    }

    public static <K, T, V> Map<K, List<V>> groupBy(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return groupBy(data, keyMapper, valueMapper, Collectors.toList(), false);
    }

    public static <K, T, V> Map<K, List<V>> groupBy(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper, boolean ignoreNullKey) {
        return groupBy(data, keyMapper, valueMapper, Collectors.toList(), ignoreNullKey);
    }

    public static <K, T, V, C> Map<K, C> groupBy(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper, Collector<V, ?, C> collector) {
        return groupBy(data, keyMapper, valueMapper, collector, false);
    }

    public static <K, T, V, E> Map<K, E> groupBy(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper, Collector<V, ?, E> collector, boolean discardNullKey) {
        return groupBy(data, keyMapper, valueMapper, collector, discardNullKey, HashMap::new);
    }

    public static <K, T, V, E> Map<K, E> groupBy(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper, Collector<V, ?, E> collector, boolean discardNullKey, Supplier<Map<K, E>> mapSupplier) {
        Stream<T> stream = from(data);
        if (discardNullKey) {
            stream = stream.filter(item -> null != keyMapper.apply(item));
        }
        return stream.collect(groupingBy(keyMapper, mapSupplier, mapping(valueMapper, collector)));
    }

    public static <T> String join(Iterable<T> data) {
        return join(data, StrPool.COMMA);
    }

    public static <T> String join(Iterable<T> data, String delimiter) {
        return join(data, delimiter, Objects::toString);
    }

    public static <T> String join(Iterable<T> data, String delimiter, Function<T, String> mapper) {
        return from(data).map(mapper).collect(joining(delimiter));
    }

    public static <T> List<T> split(String data, Function<String, T> mapper) {
        return split(data, StrPool.COMMA, mapper);
    }

    public static <T> List<T> split(String data, String delimiter, Function<String, T> mapper) {
        return Arrays.stream(StringUtils.split(data, delimiter)).map(mapper).collect(Collectors.toList());
    }

    public static <T> Stream<T> from(Iterable<T> iterable) {
        return from(iterable, false);
    }

    public static <T> Stream<T> from(Iterable<T> iterable, boolean parallel) {
        Iterable<T> source = Optional.ofNullable(iterable).orElse(emptyList());
        return StreamSupport.stream(source.spliterator(), parallel);
    }

}
