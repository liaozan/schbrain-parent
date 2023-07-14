package com.schbrain.framework.autoconfigure.cache.concurrent;

import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.ApplicationName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.schbrain.common.util.ParameterDiscoverUtils.getMethodArgsMap;

/**
 * @author liaozan
 * @see RateLimiter
 * @since 2022/5/5
 */
@Slf4j
@Aspect
public class RateLimitAspect {

    private final Map<String, RRateLimiter> rateLimiterMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, RateLimitCacheKeyVariablesContributor> contributorMap = new ConcurrentHashMap<>();

    private final String keyPrefix;
    private final RedissonClient redissonClient;
    private final ExpressionParser expressionParser;

    public RateLimitAspect(ConfigurableListableBeanFactory beanFactory, RedissonClient redissonClient) {
        this.keyPrefix = ApplicationName.get();
        this.redissonClient = redissonClient;
        this.expressionParser = new ExpressionParser(beanFactory);
    }

    @Before("@annotation(annotation)")
    public void beforeExecute(JoinPoint joinPoint, RateLimiter annotation) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        doRateLimit(annotation, joinPoint, methodSignature);
    }

    protected void doRateLimit(RateLimiter annotation, JoinPoint joinPoint, MethodSignature signature) {
        Map<String, Object> variables = prepareVariables(annotation, joinPoint, signature);

        String cacheKey = expressionParser.parse(annotation.cacheKey(), variables);
        if (cacheKey == null) {
            throw new BaseException("cacheKey should not be null");
        }

        String formattedCacheKey = formatCacheKey(cacheKey);
        RRateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(formattedCacheKey, key -> createRateLimiter(key, annotation));

        if (rateLimiter.tryAcquire()) {
            return;
        }
        throw new BaseException("The access frequency is too fast, please try again later");
    }

    protected String formatCacheKey(String cacheKey) {
        return "rateLimit:" + keyPrefix + ":" + cacheKey;
    }

    protected RRateLimiter createRateLimiter(String cacheKey, RateLimiter annotation) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(cacheKey);
        rateLimiter.setRate(RateType.PER_CLIENT, annotation.permits(), annotation.expireTime(), RateIntervalUnit.valueOf(annotation.unit().name()));
        return rateLimiter;
    }

    private Map<String, Object> prepareVariables(RateLimiter annotation, JoinPoint joinPoint, MethodSignature signature) {
        Map<String, Object> variables = getMethodArgsMap(signature.getMethod(), joinPoint.getArgs());
        Class<? extends RateLimitCacheKeyVariablesContributor> contributorClass = annotation.contributor();
        if (contributorClass == null || contributorClass == NoOpRateLimitCacheKeyVariablesContributor.class) {
            return variables;
        }

        RateLimitCacheKeyVariablesContributor contributor = contributorMap.get(contributorClass);
        if (contributor == null) {
            contributor = BeanUtils.instantiateClass(contributorClass);
            contributorMap.put(contributorClass, contributor);
        }
        Map<String, Object> contributeVariables = contributor.contribute(annotation, joinPoint, signature);
        if (MapUtils.isNotEmpty(contributeVariables)) {
            variables.putAll(contributeVariables);
        }
        return variables;
    }

}
