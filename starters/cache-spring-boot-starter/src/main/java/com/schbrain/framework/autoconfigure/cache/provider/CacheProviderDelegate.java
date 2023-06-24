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
public class CacheProviderDelegate implements CacheProvider {

    private final String prefixWithDelimiter;

    private final CacheProvider cacheProvider;

    public CacheProviderDelegate(CacheProperties properties, CacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
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

    @Override
    public boolean isExpired(String cacheKey) {
        return getCacheProvider().isExpired(withKeyPrefix(cacheKey));
    }

    @Override
    public void expire(String cacheKey, Duration expiration) {
        checkDuration(expiration);
        getCacheProvider().expire(withKeyPrefix(cacheKey), expiration);
    }

    @Override
    public Duration getExpire(String cacheKey) {
        return getCacheProvider().getExpire(withKeyPrefix(cacheKey));
    }

    @Override
    public boolean hasKey(String cacheKey) {
        return getCacheProvider().hasKey(withKeyPrefix(cacheKey));
    }

    @Override
    public Set<String> keys(String pattern, long limit) {
        Set<String> keys = getCacheProvider().keys(withKeyPrefix(pattern), limit);
        return StreamUtils.toSet(keys, this::removeKeyPrefix);
    }

    @Override
    public void del(List<String> cacheKeys) {
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return;
        }
        List<String> keysWithPrefix = StreamUtils.toList(cacheKeys, this::withKeyPrefix);
        getCacheProvider().del(keysWithPrefix);
    }

    @Override
    public <T> T get(String cacheKey, Class<T> valueType) {
        return getCacheProvider().get(withKeyPrefix(cacheKey), valueType);
    }

    @Override
    public <T> void set(String cacheKey, T value, Duration expiration) {
        checkDuration(expiration);
        getCacheProvider().set(withKeyPrefix(cacheKey), value, expiration);
    }

    @Override
    public <T> void multiSet(Map<String, T> data, Duration expiration) {
        if (MapUtils.isEmpty(data)) {
            return;
        }
        checkDuration(expiration);
        Map<String, T> dataWithPrefixedKey = Maps.newHashMapWithExpectedSize(data.size());
        for (Entry<String, T> entry : data.entrySet()) {
            dataWithPrefixedKey.put(withKeyPrefix(entry.getKey()), entry.getValue());
        }
        getCacheProvider().multiSet(dataWithPrefixedKey, expiration);
    }

    @Override
    public <T> Map<String, T> multiGet(Collection<String> cacheKeys, Class<T> valueType, boolean discardIfValueIsNull) {
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return Collections.emptyMap();
        }
        List<String> keysWithPrefix = StreamUtils.toList(cacheKeys, this::withKeyPrefix);
        Map<String, T> dataWithPrefixedKey = getCacheProvider().multiGet(keysWithPrefix, valueType, discardIfValueIsNull);
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

    @Override
    public <T> List<T> getList(String cacheKey, Class<T> valueType) {
        return getCacheProvider().getList(withKeyPrefix(cacheKey), valueType);
    }

    public CacheProvider getCacheProvider() {
        return cacheProvider;
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