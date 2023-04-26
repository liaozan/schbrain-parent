package com.schbrain.framework.autoconfigure.mybatis.constraint;

/**
 * @author liaozan
 * @since 2022/8/30
 */
public interface TableConstraintChecker {

    /**
     * 检查基础字段
     */
    void checkBasicField(Table table);

    /**
     * 检查逻辑删除字段
     */
    void checkLogicDeleteField(Table table);

}