package com.schbrain.framework.autoconfigure.starrocks.operation;

import cn.hutool.core.lang.Singleton;
import com.schbrain.framework.autoconfigure.starrocks.properties.StarrocksProperties;

/**
 * @author liaozan
 * @since 2023/11/27
 */
public class StarrocksOperationFactory {

    public static final String STREAM_LOAD_TEMPLATE = "http://%s:%s/api/%s/%s/_stream_load";

    private static final String CACHE_PREFIX = "starrocks-operation-service-%s";

    private final StarrocksProperties properties;

    public StarrocksOperationFactory(StarrocksProperties properties) {
        this.properties = properties;
    }

    public <T> StarrocksService<T> getStarrocksService(String tableName, Class<T> entityClass) {
        return getStarrocksService(properties.getDatabase(), tableName, entityClass);
    }

    public <T> StarrocksService<T> getStarrocksService(String database, String tableName, Class<T> entityClass) {
        return Singleton.get(String.format(CACHE_PREFIX, tableName), () -> new StarrocksServiceImpl<>(createStreamLoadHandler(database, tableName), entityClass));
    }

    private StarrocksStreamLoadHandler createStreamLoadHandler(String database, String tableName) {
        String streamLoadUrl = String.format(STREAM_LOAD_TEMPLATE, properties.getHost(), properties.getHttpPort(), database, tableName);
        return new StarrocksStreamLoadHandler(streamLoadUrl, properties.getUsername(), properties.getPassword());
    }

}
