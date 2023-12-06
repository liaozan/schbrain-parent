package com.schbrain.framework.autoconfigure.starrocks.annotation;

import java.lang.annotation.*;

/**
 * @author liaozan
 * @since 2023/12/6
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StarrocksTable {

    /**
     * 表名
     */
    String value();

}
