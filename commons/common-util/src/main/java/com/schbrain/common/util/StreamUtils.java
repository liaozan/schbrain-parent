package com.schbrain.common.util;

import cn.hutool.core.text.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

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

    public static <T, V, R> R filter(Iterable<T> data, Predicate<T> predicate, Function<T, V> valueMapper, Collector<V, ?, R> collector) {
        return from(data).filter(predicate).map(valueMapper).collect(collector);
    }

    public static <T, E> List<E> toList(Iterable<T> data, Function<T, E> keyMapper) {
        return toList(data, keyMapper, false);
    }

    public static <T, E, V> List<V> toList(Iterable<T> data, Function<T, E> keyMapper, Function<E, V> valueMapper) {
        return toList(data, keyMapper, valueMapper, false, false);
    }

    public static <T, E> List<E> toList(Iterable<T> data, Function<T, E> keyMapper, boolean distinct) {
        return toList(data, keyMapper, Function.identity(), distinct, false);
    }

    public static <T, E> List<E> toList(Iterable<T> data, Function<T, E> keyMapper, boolean distinct, boolean discardNull) {
        return toList(data, keyMapper, Function.identity(), distinct, discardNull);
    }

    public static <T, E, V> List<V> toList(Iterable<T> data, Function<T, E> keyMapper, Function<E, V> valueMapper, boolean distinct, boolean discardNull) {
        return extract(data, keyMapper, distinct, valueMapper, discardNull, Collectors.toList());
    }

    public static <T, E> Set<E> toSet(Iterable<T> data, Function<T, E> keyMapper) {
        return toSet(data, keyMapper, Function.identity());
    }

    public static <T, E, V> Set<V> toSet(Iterable<T> data, Function<T, E> keyMapper, Function<E, V> valueMapper) {
        return toSet(data, keyMapper, valueMapper, false);
    }

    public static <T, V> Set<V> toSet(Iterable<T> data, Function<T, V> keyMapper, boolean discardNull) {
        return toSet(data, keyMapper, Function.identity(), discardNull);
    }

    public static <T, E, V> Set<V> toSet(Iterable<T> data, Function<T, E> keyMapper, Function<E, V> valueMapper, boolean discardNull) {
        return extract(data, keyMapper, false, valueMapper, discardNull, Collectors.toSet());
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

    public static <K, V> Map<K, List<V>> groupBy(Iterable<V> data, Function<V, K> keyMapper) {
        return groupBy(data, keyMapper, false);
    }

    public static <K, T> Map<K, List<T>> groupBy(Iterable<T> data, Function<T, K> keyMapper, boolean ignoreNullKey) {
        return groupBy(data, keyMapper, Collectors.toList(), ignoreNullKey);
    }

    public static <K, T, V> Map<K, V> groupBy(Iterable<T> data, Function<T, K> keyMapper, Collector<T, ?, V> collectors) {
        return groupBy(data, keyMapper, collectors, false);
    }

    public static <K, T, V> Map<K, V> groupBy(Iterable<T> data, Function<T, K> keyMapper, Collector<T, ?, V> collectors, boolean discardNullKey) {
        return groupBy(data, keyMapper, Function.identity(), collectors, discardNullKey);
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

    public static <T> String join(Iterable<T> data, String delimiter, Function<T, String> toStringFunction) {
        return from(data).map(toStringFunction).collect(joining(delimiter));
    }

    public static List<String> split(String data) {
        return split(data, Function.identity());
    }

    public static <T> List<T> split(String data, Function<String, T> mapper) {
        return split(data, StrPool.COMMA, mapper);
    }

    public static <T> List<T> split(String data, String delimiter, Function<String, T> mapper) {
        if (StringUtils.isBlank(data)) {
            return new ArrayList<>();
        }
        return Arrays.stream(StringUtils.split(data, delimiter)).map(mapper).collect(Collectors.toList());
    }

    public static <T> Stream<T> from(Iterable<T> iterable) {
        return from(iterable, false);
    }

    public static <T> Stream<T> from(Iterable<T> iterable, boolean parallel) {
        Iterable<T> source = Optional.ofNullable(iterable).orElse(emptyList());
        return StreamSupport.stream(source.spliterator(), parallel);
    }

    private static <T, E, R, V> R extract(Iterable<T> data, Function<T, E> mapper, boolean distinct, Function<E, V> valueMapper, boolean discardNull, Collector<V, ?, R> collector) {
        Predicate<E> predicate = any -> true;
        if (discardNull) {
            predicate = Objects::nonNull;
        }
        return extract(data, mapper, predicate, distinct, valueMapper, collector);
    }

    private static <T, E, R, V> R extract(Iterable<T> data, Function<T, E> keyMapper, Predicate<E> predicate, boolean distinct, Function<E, V> valueMapper, Collector<V, ?, R> collector) {
        Stream<E> stream = from(data).map(keyMapper);
        if (distinct) {
            stream = stream.distinct();
        }
        return stream.filter(predicate).map(valueMapper).collect(collector);
    }

}
