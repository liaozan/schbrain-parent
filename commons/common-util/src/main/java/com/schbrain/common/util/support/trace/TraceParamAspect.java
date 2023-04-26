package com.schbrain.common.util.support.trace;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.lang.reflect.Method;

/**
 * @author liaozan
 * @since 2022/3/31
 */
@Slf4j
@Aspect
@ConditionalOnClass({Advisor.class, Aspect.class})
public class TraceParamAspect {

    @Before("@annotation(traceParam)")
    public void tracedMethod(JoinPoint joinPoint, TraceParam traceParam) {
        try {
            tracingParam(joinPoint, traceParam);
        } catch (Exception e) {
            log.warn("Could not extract args for method annotated with @{}", TraceParam.class.getSimpleName(), e);
        }
    }

    protected void tracingParam(JoinPoint joinPoint, TraceParam annotation) {
        if (!(joinPoint instanceof MethodSignature)) {
            return;
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> declaringType = methodSignature.getDeclaringType();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();

        String formattedArgs = annotation.shape().format(method, args);
        String content = format(method, formattedArgs);

        LoggerFactory.getLogger(declaringType).info(content);
    }

    protected String format(Method method, Object formattedArgs) {
        return String.format("%s\n%s", method.toGenericString(), formattedArgs);
    }

}