package com.schbrain.framework.autoconfigure.cache.provider.redis;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.schbrain.common.util.JacksonUtils;
import com.schbrain.framework.autoconfigure.cache.exception.CacheException;
import com.schbrain.framework.autoconfigure.cache.provider.CacheOperation;
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
 */
public class RedisCacheOperation implements CacheOperation {

    private static final int DEFAULT_BATCH_SIZE = 100;

    private final StringRedisTemplate redisTemplate;

    public RedisCacheOperation(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean hasKey(String cacheKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
    }

    @Override
    public boolean isExpired(String cacheKey) {
        return !hasKey(cacheKey);
    }

    @Override
    public void expire(String key, Duration expiration) {
        redisTemplate.expire(key, expiration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public Duration getExpire(String key) {
        Long expiration = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        if (expiration == null) {
            throw new CacheException("should not be null");
        }
        return Duration.ofMillis(expiration);
    }

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

    @Override
    public void del(List<String> cacheKeys) {
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return;
        }
        redisTemplate.delete(cacheKeys);
    }

    @Override
    public <T> T getValue(String cacheKey, Class<T> type) {
        return JacksonUtils.getObjectFromJson(getValueFromRedis(cacheKey), type);
    }

    @Override
    public <T> void setValue(String cacheKey, T value, Duration expiration) {
        setValueToRedis(cacheKey, value, expiration);
    }

    @Override
    public <T> void multiSet(Map<String, T> data, Duration expiration) {
        Iterables.partition(data.keySet(), DEFAULT_BATCH_SIZE).forEach(keys -> redisTemplate.executePipelined(multiSet(data, expiration, keys)));
    }

    @Override
    public <T> Map<String, T> multiGet(Collection<String> cacheKeys, Class<T> type, boolean discardIfValueIsNull) {
        Map<String, T> result = Maps.newHashMapWithExpectedSize(cacheKeys.size());
        Iterables.partition(cacheKeys, DEFAULT_BATCH_SIZE).forEach(subKeys -> multiGet(type, discardIfValueIsNull, subKeys, result));
        return result;
    }

    @Override
    public <T> List<T> getList(String cacheKey, Class<T> type) {
        return JacksonUtils.getListFromJson(getValueFromRedis(cacheKey), type);
    }

    private <T> void multiGet(Class<T> type, boolean discardIfValueIsNull, List<String> keys, Map<String, T> result) {
        List<String> valueList = Objects.requireNonNull(redisTemplate.opsForValue().multiGet(keys));
        for (int i = 0; i < keys.size(); i++) {
            T rawValue = JacksonUtils.getObjectFromJson(valueList.get(i), type);
            if (discardIfValueIsNull && rawValue == null) {
                continue;
            }
            result.put(keys.get(i), rawValue);
        }
    }

    private <T> RedisCallback<Void> multiSet(Map<String, T> data, Duration expiration, List<String> keys) {
        return connection -> {
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
        };
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
