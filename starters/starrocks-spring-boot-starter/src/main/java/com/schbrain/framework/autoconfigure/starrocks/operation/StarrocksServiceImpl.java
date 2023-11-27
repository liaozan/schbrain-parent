package com.schbrain.framework.autoconfigure.starrocks.operation;

import com.schbrain.common.util.ValidateUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author liaozan
 * @since 2023/11/27
 */
public class StarrocksServiceImpl<T> implements StarrocksService<T> {

    // TODO for select
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Class<T> entityClass;

    private final StarrocksStreamLoadHandler handler;

    public StarrocksServiceImpl(StarrocksStreamLoadHandler handler, Class<T> entityClass) {
        this.handler = handler;
        this.entityClass = entityClass;
    }

    @Override
    public void upsert(T entity) {
        upsert(entity, Collections.emptyList());
    }

    @Override
    public void upsert(T entity, List<String> columns) {
        ValidateUtils.notNull(entity, "entity不能为空");
        upsertBatch(List.of(entity), columns);
    }

    @Override
    public void upsertBatch(List<T> entityList) {
        upsertBatch(entityList, Collections.emptyList());
    }

    @Override
    public void upsertBatch(List<T> entityList, List<String> columns) {
        ValidateUtils.notEmpty(entityList, "entityList不能为空");
        handler.upsertBatch(entityList, columns);
    }

    @Override
    public void delete(T entity) {
        ValidateUtils.notNull(entity, "entity不能为空");
        deleteBatch(List.of(entity));
    }

    @Override
    public void deleteBatch(List<T> entityList) {
        ValidateUtils.notNull(entityList, "entityList不能为空");
        handler.deleteBatch(entityList);
    }

}
