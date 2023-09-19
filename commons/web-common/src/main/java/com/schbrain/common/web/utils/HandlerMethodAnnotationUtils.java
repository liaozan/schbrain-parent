package com.schbrain.common.web.utils;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * @author liaozan
 * @since 2022-12-15
 */
public class HandlerMethodAnnotationUtils {

    @Nullable
    public static <T extends Annotation> T getAnnotation(HandlerMethod handlerMethod, Class<T> annotationType) {
        T annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), annotationType);
        if (annotation == null) {
            annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), annotationType);
        }
        return annotation;
    }

    public static <T extends Annotation> boolean hasAnnotation(HandlerMethod handlerMethod, Class<T> annotationType) {
        boolean hasAnnotation = AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), annotationType);
        if (!hasAnnotation) {
            hasAnnotation = AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), annotationType);
        }
        return hasAnnotation;
    }

}
