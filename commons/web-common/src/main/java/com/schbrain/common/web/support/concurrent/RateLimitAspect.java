package com.schbrain.common.web.support.concurrent;

import cn.hutool.extra.spring.SpringUtil;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.ApplicationName;
import com.schbrain.common.util.ParameterDiscoverUtils;
import com.schbrain.common.util.SpelUtils;
import com.schbrain.common.web.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liaozan
 * @see com.schbrain.common.web.support.concurrent.RateLimiter
 * @since 2022/5/5
 */
@Slf4j
@Aspect
@ConditionalOnBean(StringRedisTemplate.class)
@ConditionalOnClass({Advice.class, RedisConnectionFactory.class})
public class RateLimitAspect {

    private final Map<Class<?>, RateLimitCacheKeyVariablesContributor> contributorMap = new ConcurrentHashMap<>();

    private final String keyPrefix;

    private final StringRedisTemplate stringRedisTemplate;

    public RateLimitAspect(StringRedisTemplate stringRedisTemplate) {
        this.keyPrefix = ApplicationName.get();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Before("@annotation(rateLimiter)")
    public void beforeExecute(JoinPoint joinPoint, RateLimiter rateLimiter) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        doRateLimit(rateLimiter, joinPoint, methodSignature);
    }

    protected void doRateLimit(RateLimiter rateLimiter, JoinPoint joinPoint, MethodSignature signature) {
        Map<String, Object> variables = prepareVariables(rateLimiter, joinPoint, signature);

        String cacheKey = SpelUtils.parse(rateLimiter.cacheKey(), variables, String.class);
        if (cacheKey == null) {
            throw new BaseException("cacheKey should not be null");
        }

        String formattedCacheKey = formatCacheKey(cacheKey);
        BoundValueOperations<String, String> rateLimitOps = stringRedisTemplate.boundValueOps(formattedCacheKey);

        try {
            long accessCount = Optional.ofNullable(rateLimitOps.increment()).orElse(1L);
            if (accessCount > rateLimiter.permits()) {
                throw new BaseException("访问频次太快,请稍后再试。");
            }

            if (accessCount <= 1) {
                rateLimitOps.expire(rateLimiter.expireTime(), rateLimiter.unit());
            }
        } catch (Exception ex) {
            log.error("RateLimit encountered an unknown error, remove cacheKey: {}", formattedCacheKey, ex);
            // Remove cacheKey to prevent the cache from never expiring
            stringRedisTemplate.delete(formattedCacheKey);
            throw ex;
        }
    }

    protected Map<String, Object> createEvaluationVariables(MethodSignature methodSignature, Object[] args) {
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> methodArgsMap = ParameterDiscoverUtils.getMethodArgsMap(methodSignature.getMethod(), args);
        if (!CollectionUtils.isEmpty(methodArgsMap)) {
            variables.putAll(methodArgsMap);
        }

        variables.put("request", ServletUtils.getRequest());
        variables.put("response", ServletUtils.getResponse());
        variables.put("applicationContext", SpringUtil.getApplicationContext());
        variables.put("beanFactory", SpringUtil.getBeanFactory());
        variables.put("args", args);
        return variables;
    }

    protected String formatCacheKey(String cacheKey) {
        return "rateLimit:" + keyPrefix + ":" + cacheKey;
    }

    private Map<String, Object> prepareVariables(RateLimiter rateLimiter, JoinPoint joinPoint, MethodSignature signature) {
        Map<String, Object> variables = createEvaluationVariables(signature, joinPoint.getArgs());
        Class<? extends RateLimitCacheKeyVariablesContributor> contributorClass = rateLimiter.contributor();
        if (contributorClass == null || contributorClass == NoOpRateLimitCacheKeyVariablesContributor.class) {
            return variables;
        }

        RateLimitCacheKeyVariablesContributor contributor = contributorMap.get(contributorClass);
        if (contributor == null) {
            contributor = BeanUtils.instantiateClass(contributorClass);
            contributorMap.put(contributorClass, contributor);
        }
        Map<String, Object> contributeVariables = contributor.contribute(rateLimiter, joinPoint, signature);
        if (!CollectionUtils.isEmpty(contributeVariables)) {
            variables.putAll(contributeVariables);
        }
        return variables;
    }

}