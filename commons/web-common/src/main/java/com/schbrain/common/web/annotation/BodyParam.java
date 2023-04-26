package com.schbrain.common.web.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

/**
 * @author liaozan
 * @see RequestParam
 * @since 2022-12-02
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {

    /**
     * @see RequestParam#value()
     */
    @AliasFor("name")
    String value() default "";

    /**
     * @see RequestParam#name()
     */
    @AliasFor("value")
    String name() default "";

    /**
     * @see RequestParam#required()
     */
    boolean required() default true;

    /**
     * @see RequestParam#defaultValue()
     */
    String defaultValue() default ValueConstants.DEFAULT_NONE;

}