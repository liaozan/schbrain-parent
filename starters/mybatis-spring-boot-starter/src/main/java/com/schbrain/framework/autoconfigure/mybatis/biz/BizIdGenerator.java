package com.schbrain.framework.autoconfigure.mybatis.biz;

/**
 * @author liaozan
 * @since 2023-04-17
 */
@FunctionalInterface
public interface BizIdGenerator {

    /**
     * 生成 BizId 的值
     */
    String generate(Object entity);

}