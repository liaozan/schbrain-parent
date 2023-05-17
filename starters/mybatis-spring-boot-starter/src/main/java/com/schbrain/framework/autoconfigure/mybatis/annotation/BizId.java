package com.schbrain.framework.autoconfigure.mybatis.annotation;

import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdColumnField;
import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdType;

import java.lang.annotation.*;

/**
 * @author liaozan
 * @since 2023-03-22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BizId {

    /**
     * 逻辑主键列名,为空时取字段名
     *
     * @see BizIdColumnField
     */
    String value() default "";

    /**
     * 逻辑主键类型
     */
    BizIdType type() default BizIdType.ID_WORKER;

}
