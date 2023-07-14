package com.schbrain.framework.autoconfigure.cache.concurrent;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Map;

/**
 * @author liaozan
 * @since 2022/5/5
 */
public interface RateLimitCacheKeyVariablesContributor {

    Map<String, Object> contribute(RateLimiter rateLimiter, JoinPoint joinPoint, MethodSignature signature);

}
