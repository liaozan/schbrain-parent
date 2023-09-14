package com.schbrain.common.web.annotation;

import java.lang.annotation.*;

/**
 * @author liaozan
 * @see com.schbrain.common.web.result.ResponseDTO
 * @since 2022/8/29
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseWrapOption {

    /**
     * 是否忽略返回值处理
     */
    boolean ignore() default true;

    /**
     * 是否忽略异常处理
     */
    boolean ignoreException() default true;

}
