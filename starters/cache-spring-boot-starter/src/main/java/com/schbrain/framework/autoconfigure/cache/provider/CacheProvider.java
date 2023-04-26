package com.schbrain.framework.autoconfigure.cache.provider;

import java.time.Duration;
import java.util.*;

/**
 * @author zhuyf
 * @since 2020/9/24
 **/
public interface CacheProvider {

    /**
     * 指定缓存失效时间
     */
    void expire(String cacheKey, Duration expiration);

    /**
     * 根据cacheKey 获取过期时间
     */
    Duration getExpire(String cacheKey);

    /**
     * 判断cacheKey是否存在
     */
    boolean hasKey(String cacheKey);

    /**
     * 删除缓存
     */
    void del(List<String> cacheKeys);

    /**
     * 缓存获取
     */
    <T> T get(String cacheKey, Class<T> valueType);

    /**
     * 缓存获取
     */
    <T> Map<String, T> multiGet(Collection<String> cacheKeys, Class<T> valueType, boolean discardIfValueIsNull);

    /**
     * list 缓存获取
     */
    <T> List<T> getList(String cacheKey, Class<T> valueType);

    /**
     * 缓存放入并设置时间
     */
    <T> void set(String cacheKey, T value, Duration expiration);

    /**
     * 缓存放入并设置时间
     */
    <T> void multiSet(Map<String, T> data, Duration expiration);

    /**
     * 查询key是否过期
     */
    boolean isExpired(String cacheKey);

}