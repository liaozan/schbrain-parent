package com.schbrain.common.web.support.concurrent;

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
     * 缓存的 key,使用 spring el 进行解析
     */
    String cacheKey();

    /**
     * evaluation variables contributor
     */
    Class<? extends RateLimitCacheKeyVariablesContributor> contributor() default NoOpRateLimitCacheKeyVariablesContributor.class;

}
