package com.schbrain.framework.autoconfigure.cache.provider;

import com.google.common.collect.Maps;
import com.schbrain.common.util.ApplicationName;
import com.schbrain.common.util.StreamUtils;
import com.schbrain.framework.autoconfigure.cache.exception.CacheException;
import com.schbrain.framework.autoconfigure.cache.properties.CacheProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author liaozan
 * @since 2022/8/1
 */
public class PrefixedCacheOperation {

    private final CacheOperation cacheOperation;
    private final String prefixWithDelimiter;

    public PrefixedCacheOperation(CacheProperties properties, CacheOperation cacheOperation) {
        this.cacheOperation = cacheOperation;
        if (properties.isAppendPrefix()) {
            String prefix = properties.getPrefix();
            if (StringUtils.isBlank(prefix)) {
                prefix = ApplicationName.get();
            }
            this.prefixWithDelimiter = prefix + properties.getDelimiter();
        } else {
            this.prefixWithDelimiter = null;
        }
    }

    public boolean hasKey(String cacheKey) {
        return cacheOperation.hasKey(withKeyPrefix(cacheKey));
    }

    public boolean isExpired(String cacheKey) {
        return cacheOperation.isExpired(withKeyPrefix(cacheKey));
    }

    public void expire(String cacheKey, Duration expiration) {
        checkDuration(expiration);
        cacheOperation.expire(withKeyPrefix(cacheKey), expiration);
    }

    public Duration getExpire(String cacheKey) {
        return cacheOperation.getExpire(withKeyPrefix(cacheKey));
    }

    public Set<String> keys(String pattern, long limit) {
        Set<String> keys = cacheOperation.keys(withKeyPrefix(pattern), limit);
        return StreamUtils.toSet(keys, this::removeKeyPrefix);
    }

    public void del(List<String> cacheKeys) {
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return;
        }
        List<String> keysWithPrefix = StreamUtils.toList(cacheKeys, this::withKeyPrefix);
        cacheOperation.del(keysWithPrefix);
    }

    public <T> T getValue(String cacheKey, Class<T> valueType) {
        return cacheOperation.getValue(withKeyPrefix(cacheKey), valueType);
    }

    public <T> void setValue(String cacheKey, T value, Duration expiration) {
        checkDuration(expiration);
        cacheOperation.setValue(withKeyPrefix(cacheKey), value, expiration);
    }

    public <T> void multiSet(Map<String, T> data, Duration expiration) {
        if (MapUtils.isEmpty(data)) {
            return;
        }
        checkDuration(expiration);
        Map<String, T> dataWithPrefixedKey = Maps.newHashMapWithExpectedSize(data.size());
        for (Entry<String, T> entry : data.entrySet()) {
            dataWithPrefixedKey.put(withKeyPrefix(entry.getKey()), entry.getValue());
        }
        cacheOperation.multiSet(dataWithPrefixedKey, expiration);
    }

    public <T> Map<String, T> multiGet(Collection<String> cacheKeys, Class<T> valueType, boolean discardIfValueIsNull) {
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return Collections.emptyMap();
        }
        List<String> keysWithPrefix = StreamUtils.toList(cacheKeys, this::withKeyPrefix);
        Map<String, T> dataWithPrefixedKey = cacheOperation.multiGet(keysWithPrefix, valueType, discardIfValueIsNull);
        if (MapUtils.isEmpty(dataWithPrefixedKey)) {
            return Collections.emptyMap();
        } else {
            Map<String, T> result = Maps.newHashMapWithExpectedSize(dataWithPrefixedKey.size());
            // 这里不能用 Stream toMap, toMap 不允许 value 是 null
            for (Entry<String, T> cacheEntry : dataWithPrefixedKey.entrySet()) {
                result.put(removeKeyPrefix(cacheEntry.getKey()), cacheEntry.getValue());
            }
            return result;
        }
    }

    public <T> List<T> getList(String cacheKey, Class<T> valueType) {
        return cacheOperation.getList(withKeyPrefix(cacheKey), valueType);
    }

    protected String withKeyPrefix(String cacheKey) {
        if (StringUtils.isBlank(prefixWithDelimiter)) {
            return cacheKey;
        }
        return prefixWithDelimiter + cacheKey;
    }

    protected String removeKeyPrefix(String cacheKey) {
        if (StringUtils.isBlank(cacheKey)) {
            return cacheKey;
        }
        return StringUtils.removeStart(cacheKey, prefixWithDelimiter);
    }

    protected void checkDuration(Duration expiration) {
        if (expiration.isZero() || expiration.isNegative()) {
            throw new CacheException("expiration must be a positive number");
        }
    }

}
