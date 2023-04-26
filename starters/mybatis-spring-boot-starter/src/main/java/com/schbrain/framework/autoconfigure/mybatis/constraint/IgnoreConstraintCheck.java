package com.schbrain.framework.autoconfigure.mybatis.constraint;

import java.lang.annotation.*;

/**
 * @author liaozan
 * @since 2023-03-18
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreConstraintCheck {

}