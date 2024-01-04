package com.schbrain.framework.autoconfigure.starrocks.operation;

import java.util.*;

/**
 * @author liaozan
 * @since 2023/11/27
 */
public interface StarrocksService<T> {

    /**
     * 单个保存/更新
     */
    void upsert(T entity);

    /**
     * 批量保存/更新
     */
    void upsertBatch(Collection<T> entityList);

    /**
     * 单个保存/更新,传入 columns 只会处理相应的 columns
     */
    void upsert(T entity, List<String> columns);

    /**
     * 批量保存/更新,传入 columns 只会处理相应的 columns
     */
    void upsertBatch(Collection<T> entityList, List<String> columns);

    /**
     * 删除
     */
    void delete(T entity);

    /**
     * 批量删除
     */
    void deleteBatch(Collection<T> entityList);

    /**
     * 根据 sql 查询
     */
    List<T> search(String sql, Map<String, Object> params);

}
