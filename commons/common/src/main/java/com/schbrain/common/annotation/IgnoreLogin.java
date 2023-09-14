package com.schbrain.common.annotation;

import java.lang.annotation.*;

/**
 * 免登注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface IgnoreLogin {

    /**
     * 是否忽略登录
     */
    boolean ignore() default true;

}
