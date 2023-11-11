package com.schbrain.framework.autoconfigure.mybatis.base;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.*;
import java.util.function.Supplier;

public interface BaseService<T extends BaseEntity> extends IService<T> {

    /**
     * 根据 id 获取记录
     */
    T getById(Long id, boolean throwIfNotFound);

    /**
     * 根据 id 获取记录
     */
    T getById(Long id, Supplier<? extends RuntimeException> notFoundSupplier);

    /**
     * 根据 id 获取记录
     */
    <V> V getById(Long id, SFunction<T, V> column);

    /**
     * 根据 id 获取记录
     */
    <V> V getById(Long id, SFunction<T, V> column, boolean throwIfNotFound);

    /**
     * 根据 id 获取记录
     */
    <V> V getById(Long id, SFunction<T, V> column, Supplier<? extends RuntimeException> notFoundSupplier);

    /**
     * 根据 id 获取
     */
    Map<Long, T> getMapByIds(Collection<Long> ids);

    /**
     * 根据 id 获取
     */
    <V> Map<Long, V> getMapByIds(Collection<Long> ids, SFunction<T, V> column);

    /**
     * 根据业务主键获取记录
     */
    T getByBizId(Object bizId);

    /**
     * 根据业务主键获取记录
     */
    T getByBizId(Object bizId, boolean throwsIfNotFound);

    /**
     * 根据业务主键获取记录
     */
    T getByBizId(Object bizId, Supplier<? extends RuntimeException> notFoundSupplier);

    /**
     * 根据业务主键获取记录
     */
    <V> V getByBizId(Object bizId, SFunction<T, V> column);

    /**
     * 根据业务主键获取记录
     */
    <V> V getByBizId(Object bizId, SFunction<T, V> column, boolean throwsIfNotFound);

    /**
     * 根据业务主键获取记录
     */
    <V> V getByBizId(Object bizId, SFunction<T, V> column, Supplier<? extends RuntimeException> notFoundSupplier);

    /**
     * 根据业务主键获取
     */
    <V> List<V> listByIds(Collection<Long> ids, SFunction<T, V> column);

    /**
     * 根据业务主键获取
     */
    <K> List<T> listByBizIds(Collection<K> bizIds);

    /**
     * 根据业务主键获取
     */
    <K, V> List<V> listByBizIds(Collection<K> bizIds, SFunction<T, V> column);

    /**
     * 根据业务主键获取
     */
    <K> Map<K, T> getMapByBizIds(Collection<K> bizIds);

    /**
     * 根据业务主键获取
     */
    <K, V> Map<K, V> getMapByBizIds(Collection<K> bizIds, SFunction<T, V> column);

    /**
     * 根据 id 更新,null 会被更新为 null
     */
    boolean updateByIdWithNull(T entity);

    /**
     * 根据 id 批量更新,null 会被更新为 null, 默认批量大小 1000
     */
    boolean updateBatchByIdsWithNull(Collection<T> entityList);

    /**
     * 根据 id 批量更新,null 会被更新为 null
     */
    boolean updateBatchByIdsWithNull(Collection<T> entityList, int batchSize);

    /**
     * 根据业务主键删除
     */
    <K> boolean removeByBizId(K bizId);

    /**
     * 根据业务主键删除
     */
    <K> boolean removeBatchByBizIds(Collection<K> bizIds);

}
