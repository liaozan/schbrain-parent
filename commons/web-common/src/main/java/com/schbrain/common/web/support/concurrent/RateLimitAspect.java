package com.schbrain.common.web.support.concurrent;

import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.ApplicationName;
import com.schbrain.common.util.SpelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.schbrain.common.util.ParameterDiscoverUtils.getMethodArgsMap;

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
    private final BeanFactory beanFactory;
    private final StringRedisTemplate stringRedisTemplate;

    public RateLimitAspect(StringRedisTemplate stringRedisTemplate, BeanFactory beanFactory) {
        this.keyPrefix = ApplicationName.get();
        this.beanFactory = beanFactory;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Before("@annotation(rateLimiter)")
    public void beforeExecute(JoinPoint joinPoint, RateLimiter rateLimiter) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        doRateLimit(rateLimiter, joinPoint, methodSignature);
    }

    protected void doRateLimit(RateLimiter rateLimiter, JoinPoint joinPoint, MethodSignature signature) {
        Map<String, Object> variables = prepareVariables(rateLimiter, joinPoint, signature);

        String cacheKey = SpelUtils.parse(rateLimiter.cacheKey(), variables, String.class, beanFactory);
        if (cacheKey == null) {
            throw new BaseException("cacheKey should not be null");
        }

        String formattedCacheKey = formatCacheKey(cacheKey);
        BoundValueOperations<String, String> rateLimitOps = stringRedisTemplate.boundValueOps(formattedCacheKey);

        try {
            long accessCount = Optional.ofNullable(rateLimitOps.increment()).orElse(1L);
            if (accessCount > rateLimiter.permits()) {
                throw new BaseException("The access frequency is too fast, please try again later");
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

    protected String formatCacheKey(String cacheKey) {
        return "rateLimit:" + keyPrefix + ":" + cacheKey;
    }

    private Map<String, Object> prepareVariables(RateLimiter rateLimiter, JoinPoint joinPoint, MethodSignature signature) {
        Map<String, Object> variables = getMethodArgsMap(signature.getMethod(), joinPoint.getArgs());
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
        if (MapUtils.isNotEmpty(contributeVariables)) {
            variables.putAll(contributeVariables);
        }
        return variables;
    }

}
