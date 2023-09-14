package com.schbrain.framework.dao.mybatis.annotation;

import java.lang.annotation.*;

/**
 * description
 *
 * @author liwu on 2019/8/1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MapperConfig {

    Class<?> domainClass();

    String tableName();

}
