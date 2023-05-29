package com.schbrain.common.util;

import lombok.extern.slf4j.Slf4j;

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
        return filter(data, predicate, Collectors.toList());
    }

    public static <T> Set<T> filterToSet(Iterable<T> data, Predicate<T> predicate) {
        return filter(data, predicate, Collectors.toSet());
    }

    public static <T, C extends Iterable<T>> C filter(Iterable<T> data, Predicate<T> predicate, Collector<T, ?, C> collector) {
        return from(data).filter(predicate).collect(collector);
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

    public static <K, T> Map<K, T> toMap(Iterable<T> data, Function<T, K> keyMapper) {
        return toMap(data, keyMapper, false);
    }

    public static <K, T> Map<K, T> toMap(Iterable<T> data, Function<T, K> keyMapper, boolean ordered) {
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
        return from(data).collect(Collectors.toMap(keyMapper, valueMapper, (oldValue, newValue) -> {
            // Could not get the key when mergeFunction invoke
            log.warn("There are multiple values with the same key when toMap, return the old one");
            return oldValue;
        }, mapFactory));
    }

    public static <K, T> Map<K, List<T>> groupBy(Iterable<T> data, Function<T, K> mapper) {
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

    public static <K, T, V, C> Map<K, C> groupBy(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper, Collector<V, ?, C> collector, boolean discardNullKey) {
        return groupBy(data, keyMapper, valueMapper, collector, discardNullKey, HashMap::new);
    }

    public static <K, T, V, C> Map<K, C> groupBy(Iterable<T> data, Function<T, K> keyMapper, Function<T, V> valueMapper, Collector<V, ?, C> collector, boolean discardNullKey, Supplier<Map<K, C>> mapSupplier) {
        Stream<T> stream = from(data);
        if (discardNullKey) {
            stream = stream.filter(item -> null != keyMapper.apply(item));
        }
        return stream.collect(groupingBy(keyMapper, mapSupplier, mapping(valueMapper, collector)));
    }

    public static <T> String join(Iterable<T> data, CharSequence delimiter) {
        return join(data, delimiter, Objects::toString);
    }

    public static <T> String join(Iterable<T> data, CharSequence delimiter, Function<T, ? extends CharSequence> toStringFunction) {
        return join(data, delimiter, "", "", toStringFunction);
    }

    public static <T> String join(Iterable<T> data, CharSequence delimiter, String prefix, String suffix, Function<T, ? extends CharSequence> toStringFunction) {
        return from(data).map(toStringFunction).collect(joining(delimiter, prefix, suffix));
    }

    public static <T> Stream<T> from(Iterable<T> iterable) {
        return from(iterable, false);
    }

    public static <T> Stream<T> from(Iterable<T> iterable, boolean parallel) {
        Iterable<T> source = Optional.ofNullable(iterable).orElse(emptyList());
        return StreamSupport.stream(source.spliterator(), parallel);
    }

}
