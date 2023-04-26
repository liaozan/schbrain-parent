package com.schbrain.framework.autoconfigure.mybatis.base;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.*;
import java.util.function.Supplier;

public interface BaseService<T extends BaseEntity> extends IService<T> {

    /**
     * 根据 id 获取记录
     *
     * @param throwIfNotFound 未获取到记录时是否抛异常
     */
    T getById(Long id, boolean throwIfNotFound);

    /**
     * 根据 id 获取记录
     *
     * @param notFoundSupplier 未获取到记录时的异常处理
     */
    T getById(Long id, Supplier<? extends RuntimeException> notFoundSupplier);

    /**
     * 根据 id 获取
     */
    Map<Long, T> getMapByIds(Collection<Long> ids);

    /**
     * 根据业务主键获取记录
     */
    T getByBizId(String bizId);

    /**
     * 根据业务主键获取记录
     *
     * @param throwIfNotFound 未获取到记录时是否抛异常
     */
    T getByBizId(String bizId, boolean throwIfNotFound);

    /**
     * 根据业务主键获取记录
     *
     * @param notFoundSupplier 未获取到记录时是否抛异常
     */
    T getByBizId(String bizId, Supplier<? extends RuntimeException> notFoundSupplier);

    /**
     * 根据业务主键获取
     */
    List<T> listByBizIds(Collection<String> bizIds);

    /**
     * 根据业务主键获取
     */
    Map<String, T> getMapByBizIds(Collection<String> bizIds);

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

}