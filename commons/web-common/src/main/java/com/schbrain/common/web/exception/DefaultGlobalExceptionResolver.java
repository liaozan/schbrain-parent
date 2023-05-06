package com.schbrain.common.web.exception;

import com.schbrain.common.web.annotation.ResponseWrapOption;
import com.schbrain.common.web.properties.WebProperties;
import com.schbrain.common.web.utils.HandlerMethodAnnotationUtils;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.method.support.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerMethodExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liaozan
 * @since 2022/8/30
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class DefaultGlobalExceptionResolver extends AbstractHandlerMethodExceptionResolver {

    private final WebProperties webProperties;

    private final GlobalExceptionHandler exceptionHandler;

    private final ExceptionHandlerMethodResolver handlerMethodResolver;

    private final HandlerMethodArgumentResolverComposite argumentResolverComposite;

    private final HandlerMethodReturnValueHandlerComposite returnValueHandlerComposite;

    private final Map<Class<?>, ExceptionHandlerMethodResolver> exceptionHandlerCache = new ConcurrentHashMap<>(64);

    public DefaultGlobalExceptionResolver(ExceptionHandlerExceptionResolver handlerMethodResolver, WebProperties webProperties, GlobalExceptionHandler exceptionHandler) {
        this.webProperties = webProperties;
        this.exceptionHandler = exceptionHandler;
        this.handlerMethodResolver = new ExceptionHandlerMethodResolver(exceptionHandler.getClass());
        this.argumentResolverComposite = handlerMethodResolver.getArgumentResolvers();
        this.returnValueHandlerComposite = handlerMethodResolver.getReturnValueHandlers();
    }

    @Override
    protected boolean shouldApplyTo(HttpServletRequest request, @Nullable Object handler) {
        if (!webProperties.isWrapResponse()) {
            return false;
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            ResponseWrapOption responseWrapOption = HandlerMethodAnnotationUtils.getAnnotation(handlerMethod, ResponseWrapOption.class);

            if (responseWrapOption == null) {
                return true;
            }
            return Boolean.FALSE.equals(responseWrapOption.ignoreException());
        }
        return true;
    }

    @Override
    protected final ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handlerMethod, Exception exception) {
        ServletInvocableHandlerMethod exceptionHandlerMethod = createExceptionHandlerMethod(exception, handlerMethod, exceptionHandler);
        if (exceptionHandlerMethod == null) {
            return null;
        }

        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        return doResolveException(webRequest, exceptionHandlerMethod, getArguments(exception, handlerMethod));
    }

    protected final ModelAndView doResolveException(ServletWebRequest webRequest, ServletInvocableHandlerMethod targetMethod, Object[] arguments) {
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        try {
            targetMethod.invokeAndHandle(webRequest, mavContainer, arguments);
        } catch (Exception e) {
            log.warn("Failure in @ExceptionHandler " + targetMethod, e);
            return null;
        }
        if (mavContainer.isRequestHandled()) {
            return new ModelAndView();
        }
        return null;
    }

    protected ServletInvocableHandlerMethod createExceptionHandlerMethod(Exception exception, @Nullable HandlerMethod handlerMethod, Object handler) {
        Method targetMethod = resolveTargetMethod(exception, handlerMethod);
        if (targetMethod == null) {
            return null;
        }
        ServletInvocableHandlerMethod exceptionHandlerMethod = new ServletInvocableHandlerMethod(handler, targetMethod);
        exceptionHandlerMethod.setHandlerMethodArgumentResolvers(argumentResolverComposite);
        exceptionHandlerMethod.setHandlerMethodReturnValueHandlers(returnValueHandlerComposite);
        return exceptionHandlerMethod;
    }

    protected Method resolveTargetMethod(Exception exception, @Nullable HandlerMethod handlerMethod) {
        Method resolvedMethod = null;
        if (handlerMethod != null) {
            Class<?> handlerType = handlerMethod.getBeanType();
            resolvedMethod = getHandlerMethodResolver(handlerType).resolveMethod(exception);
        }
        if (resolvedMethod == null) {
            resolvedMethod = handlerMethodResolver.resolveMethod(exception);
        }
        return resolvedMethod;
    }

    @Override
    protected void logException(Exception ex, HttpServletRequest request) {
        // nothing to do
    }

    private ExceptionHandlerMethodResolver getHandlerMethodResolver(Class<?> handlerType) {
        return exceptionHandlerCache.computeIfAbsent(handlerType, key -> new ExceptionHandlerMethodResolver(handlerType));
    }

    /**
     * copy from spring
     */
    private Object[] getArguments(Exception exception, HandlerMethod handlerMethod) {
        List<Throwable> exceptions = new ArrayList<>();
        Throwable exToExpose = exception;
        while (exToExpose != null) {
            exceptions.add(exToExpose);
            Throwable cause = exToExpose.getCause();
            exToExpose = (cause != exToExpose ? cause : null);
        }
        Object[] arguments = new Object[exceptions.size() + 1];
        exceptions.toArray(arguments);
        arguments[arguments.length - 1] = handlerMethod;
        return arguments;
    }

}