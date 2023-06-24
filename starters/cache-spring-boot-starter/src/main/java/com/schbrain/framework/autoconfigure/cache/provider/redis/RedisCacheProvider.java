package com.schbrain.framework.autoconfigure.cache.provider.redis;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.schbrain.common.util.JacksonUtils;
import com.schbrain.framework.autoconfigure.cache.exception.CacheException;
import com.schbrain.framework.autoconfigure.cache.provider.CacheProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuyf
 * @since 2022/7/25
 **/
@Slf4j
public class RedisCacheProvider implements CacheProvider {

    private static final int DEFAULT_BATCH_SIZE = 100;

    private final StringRedisTemplate redisTemplate;

    public RedisCacheProvider(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 查询key是否过期
     */
    @Override
    public boolean isExpired(String cacheKey) {
        return !hasKey(cacheKey);
    }

    /**
     * 指定缓存失效时间
     */
    @Override
    public void expire(String key, Duration expiration) {
        redisTemplate.expire(key, expiration.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 根据key 获取过期时间
     */
    @Override
    public Duration getExpire(String key) {
        Long expiration = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        if (expiration == null) {
            throw new CacheException("should not be null");
        }
        return Duration.ofMillis(expiration);
    }

    /**
     * 判断key是否存在
     */
    @Override
    public boolean hasKey(String cacheKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
    }

    /**
     * 模糊搜索 key
     */
    @Override
    public Set<String> keys(String pattern, long limit) {
        Set<String> keys = new HashSet<>();
        RedisSerializer<?> keySerializer = redisTemplate.getKeySerializer();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).count(limit).build();
        Cursor<byte[]> cursor = Objects.requireNonNull(redisTemplate.execute(connection -> connection.scan(scanOptions), true));
        while (cursor.hasNext()) {
            Object deserialized = keySerializer.deserialize(cursor.next());
            if (deserialized != null) {
                keys.add(deserialized.toString());
            }
        }
        return keys;
    }

    /**
     * 删除缓存
     */
    @Override
    public void del(List<String> cacheKeys) {
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return;
        }
        redisTemplate.delete(cacheKeys);
    }

    /**
     * 缓存获取
     */
    @Override
    public <T> T get(String cacheKey, Class<T> type) {
        return JacksonUtils.getObjectFromJson(getValueFromRedis(cacheKey), type);
    }

    /**
     * 普通缓存放入并设置时间
     */
    @Override
    public <T> void set(String cacheKey, T value, Duration expiration) {
        setValueToRedis(cacheKey, value, expiration);
    }

    @Override
    public <T> void multiSet(Map<String, T> data, Duration expiration) {
        Iterables.partition(data.keySet(), DEFAULT_BATCH_SIZE).forEach(keys ->
                redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                    Map<byte[], byte[]> byteMap = Maps.newHashMapWithExpectedSize(keys.size());
                    for (String key : keys) {
                        byteMap.put(key.getBytes(StandardCharsets.UTF_8), JacksonUtils.writeObjectAsBytes(data.get(key)));
                    }
                    connection.mSet(byteMap);
                    long expirationMillis = expiration.toMillis();
                    for (byte[] rawKey : byteMap.keySet()) {
                        connection.pExpire(rawKey, expirationMillis);
                    }
                    return null;
                }));
    }

    @Override
    public <T> Map<String, T> multiGet(Collection<String> cacheKeys, Class<T> type, boolean discardIfValueIsNull) {
        Map<String, T> result = Maps.newHashMapWithExpectedSize(cacheKeys.size());
        Iterables.partition(cacheKeys, DEFAULT_BATCH_SIZE).forEach(subKeys -> {
            List<String> valueList = Objects.requireNonNull(redisTemplate.opsForValue().multiGet(subKeys));
            for (int i = 0; i < subKeys.size(); i++) {
                T rawValue = JacksonUtils.getObjectFromJson(valueList.get(i), type);
                if (discardIfValueIsNull && rawValue == null) {
                    continue;
                }
                result.put(subKeys.get(i), rawValue);
            }
        });
        return result;
    }

    /**
     * list 缓存获取
     */
    @Override
    public <T> List<T> getList(String cacheKey, Class<T> type) {
        return JacksonUtils.getListFromJson(getValueFromRedis(cacheKey), type);
    }

    private String getValueFromRedis(String cacheKey) {
        if (StringUtils.isBlank(cacheKey)) {
            return null;
        }
        return redisTemplate.opsForValue().get(cacheKey);
    }

    private <T> void setValueToRedis(String cacheKey, T value, Duration expiration) {
        String cacheDate = JacksonUtils.toJsonString(value);
        redisTemplate.opsForValue().set(cacheKey, cacheDate, expiration.toMillis(), TimeUnit.MILLISECONDS);
    }

}