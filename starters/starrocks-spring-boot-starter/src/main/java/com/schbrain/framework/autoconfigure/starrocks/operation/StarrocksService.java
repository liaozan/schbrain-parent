package com.schbrain.framework.autoconfigure.starrocks.operation;

import java.util.List;

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
     * 单个保存/更新,传入 columns 只会处理响应的 column
     */
    void upsert(T entity, List<String> columns);

    /**
     * 批量保存/更新
     */
    void upsertBatch(List<T> entityList);

    /**
     * 批量保存/更新,传入 columns 只会处理响应的 column
     */
    void upsertBatch(List<T> entityList, List<String> columns);

    /**
     * 删除
     */
    void delete(T entity);

    /**
     * 批量删除
     */
    void deleteBatch(List<T> entityList);

}
