package com.schbrain.common.web.support.concurrent;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Collections;
import java.util.Map;

/**
 * @author liaozan
 * @since 2022/5/5
 */
public class NoOpRateLimitCacheKeyVariablesContributor implements RateLimitCacheKeyVariablesContributor {

    @Override
    public Map<String, Object> contribute(RateLimiter rateLimiter, JoinPoint joinPoint, MethodSignature signature) {
        return Collections.emptyMap();
    }

}