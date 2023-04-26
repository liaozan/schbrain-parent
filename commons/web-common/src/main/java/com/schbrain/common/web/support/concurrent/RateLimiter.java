package com.schbrain.common.web.support.concurrent;

import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liaozan
 * @since 2022/5/5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 过期时间
     */
    long expireTime() default 10;

    /**
     * 过期时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 过期时间内允许的许可数
     */
    int permits() default 3;

    /**
     * 缓存的 key,使用 spel 进行解析
     * <p>
     * 可用的变量 {@link  RateLimitAspect#createEvaluationVariables(MethodSignature, Object[])}
     */
    String cacheKey();

    /**
     * evaluation variables contributor
     */
    Class<? extends RateLimitCacheKeyVariablesContributor> contributor() default NoOpRateLimitCacheKeyVariablesContributor.class;

}