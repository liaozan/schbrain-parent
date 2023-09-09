package com.schbrain.framework.autoconfigure.cache;

import com.schbrain.framework.autoconfigure.cache.exception.CacheException;
import com.schbrain.framework.autoconfigure.cache.provider.PrefixedCacheOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;

@Slf4j
public class CacheUtils {

    private static PrefixedCacheOperation cacheOperation;

    public static PrefixedCacheOperation getCacheOperation() {
        if (cacheOperation == null) {
            throw new CacheException("CacheOperation is null, Please ensure the cache config is correct");
        }
        return cacheOperation;
    }

    public static void setCacheOperation(PrefixedCacheOperation cacheOperation) {
        CacheUtils.cacheOperation = cacheOperation;
    }

    /**
     * 缓存是否存在
     */
    public static boolean hasKey(String cacheKey) {
        return getCacheOperation().hasKey(cacheKey);
    }

    /**
     * 缓存是否过期
     */
    public static boolean isExpired(String cacheKey) {
        return getCacheOperation().isExpired(cacheKey);
    }

    /**
     * 设置过期时间
     */
    public static void expire(String cacheKey, Duration expiration) {
        getCacheOperation().expire(cacheKey, expiration);
    }

    /**
     * 获取过期时间
     */
    public static Duration getExpire(String cacheKey) {
        return getCacheOperation().getExpire(cacheKey);
    }

    /**
     * 模糊搜索 key, 默认采用 scan 实现
     */
    public static Set<String> keys(String pattern) {
        return keys(pattern, Long.MAX_VALUE);
    }

    /**
     * 模糊搜索 key, 默认采用 scan 实现
     */
    public static Set<String> keys(String pattern, long limit) {
        return getCacheOperation().keys(pattern, limit);
    }

    /**
     * 获取缓存数据
     */
    public static <T> T getValue(String cacheKey, Class<T> valueType) {
        return getCacheOperation().getValue(cacheKey, valueType);
    }

    /**
     * 设置缓存数据
     */
    public static <T> void putValue(String cacheKey, T value, Duration expiration) {
        getCacheOperation().setValue(cacheKey, value, expiration);
    }

    /**
     * 获取缓存数据列表
     */
    public static <T> List<T> getList(String cacheKey, Class<T> valueType) {
        return getCacheOperation().getList(cacheKey, valueType);
    }

    /**
     * 设置缓存数据列表
     */
    public static <T> void putList(String cacheKey, List<T> value, Duration expiration) {
        getCacheOperation().setValue(cacheKey, value, expiration);
    }

    /**
     * 设置新的缓存,返回旧的缓存
     */
    public static <T> T getAndSet(String cacheKey, T value, Class<T> valueType, Duration expiration) {
        T cachedData = getValue(cacheKey, valueType);
        putValue(cacheKey, value, expiration);
        return cachedData;
    }

    /**
     * 设置新的缓存,返回旧的缓存
     */
    public static <T> List<T> getAndSet(String cacheKey, List<T> value, Class<T> valueType, Duration expiration) {
        List<T> cachedData = getList(cacheKey, valueType);
        putList(cacheKey, value, expiration);
        return cachedData;
    }

    /**
     * 缓存List
     */
    public static <T> List<T> getListIfPresent(String cacheKey, Duration expiration, Class<T> valueType, Supplier<List<T>> dataLoader) {
        List<T> cachedData = getList(cacheKey, valueType);
        if (CollectionUtils.isNotEmpty(cachedData)) {
            return cachedData;
        }
        List<T> dataList = dataLoader.get();
        if (CollectionUtils.isEmpty(dataList)) {
            log.warn("The cacheKey {} did not get value from the cache, and the dataLoader returned empty list", cacheKey);
        } else {
            putList(cacheKey, dataList, expiration);
        }
        return dataList;
    }

    /**
     * 缓存数据
     */
    public static <T> T getValueIfPresent(String cacheKey, Duration expiration, Class<T> valueType, Supplier<T> dataLoader) {
        T cachedData = getValue(cacheKey, valueType);
        if (cachedData != null) {
            return cachedData;
        }
        cachedData = dataLoader.get();
        if (cachedData == null) {
            log.warn("The cacheKey {} did not get value from the cache, and the dataLoader returned empty value", cacheKey);
        } else {
            putValue(cacheKey, cachedData, expiration);
        }
        return cachedData;
    }

    /**
     * 批量获取
     */
    public static <T> Map<String, T> multiGet(Collection<String> cacheKeys, Class<T> valueType) {
        return multiGet(cacheKeys, valueType, true);
    }

    /**
     * 批量获取
     */
    public static <T> Map<String, T> multiGet(Collection<String> cacheKeys, Class<T> valueType, boolean discardIfValueIsNull) {
        return getCacheOperation().multiGet(cacheKeys, valueType, discardIfValueIsNull);
    }

    /**
     * 批量设置
     */
    public static <T> void multiSet(Map<String, T> data, Duration expiration) {
        getCacheOperation().multiSet(data, expiration);
    }

    /**
     * 删除缓存
     */
    public static void del(String... cacheKeys) {
        del(Arrays.asList(cacheKeys));
    }

    /**
     * 删除缓存
     */
    public static void del(List<String> cacheKeys) {
        getCacheOperation().del(cacheKeys);
    }

}
