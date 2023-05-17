package com.schbrain.framework.autoconfigure.mybatis.biz;

import com.schbrain.common.util.IdWorker;
import lombok.AllArgsConstructor;

/**
 * @author liaozan
 * @since 2023-04-17
 */
@AllArgsConstructor
public enum BizIdType {

    /**
     * 用户输入
     */
    INPUT(entity -> null),

    /**
     * idWorker
     */
    ID_WORKER(entity -> IdWorker.getIdStr());

    /**
     * bizId 生成
     */
    private final BizIdGenerator generator;

    /**
     * 生成 bizId 的值
     */
    public Object generateBizId(Object entity) {
        return generator.generate(entity);
    }

}
