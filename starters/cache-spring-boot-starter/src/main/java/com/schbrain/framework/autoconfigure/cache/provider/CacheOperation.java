package com.schbrain.framework.autoconfigure.cache.provider;

import java.time.Duration;
import java.util.*;

/**
 * @author zhuyf
 * @since 2020/9/24
 */
public interface CacheOperation {

    /**
     * 判断cacheKey是否存在
     */
    boolean hasKey(String cacheKey);

    /**
     * 查询key是否过期
     */
    boolean isExpired(String cacheKey);

    /**
     * 指定缓存失效时间
     */
    void expire(String cacheKey, Duration expiration);

    /**
     * 根据cacheKey 获取过期时间
     */
    Duration getExpire(String cacheKey);

    /**
     * 模糊搜索 key, 默认采用 scan 实现
     */
    Set<String> keys(String pattern, long limit);

    /**
     * 删除缓存
     */
    void del(List<String> cacheKeys);

    /**
     * 缓存获取
     */
    <T> T getValue(String cacheKey, Class<T> valueType);

    /**
     * 缓存放入并设置时间
     */
    <T> void setValue(String cacheKey, T value, Duration expiration);

    /**
     * 缓存放入并设置时间
     */
    <T> void multiSet(Map<String, T> data, Duration expiration);

    /**
     * 缓存获取
     */
    <T> Map<String, T> multiGet(Collection<String> cacheKeys, Class<T> valueType, boolean discardIfValueIsNull);

    /**
     * list 缓存获取
     */
    <T> List<T> getList(String cacheKey, Class<T> valueType);

}
