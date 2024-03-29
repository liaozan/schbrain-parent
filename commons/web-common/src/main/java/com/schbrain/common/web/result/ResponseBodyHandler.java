package com.schbrain.common.web.result;

import com.schbrain.common.web.annotation.ResponseWrapOption;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * @author liaozan
 * @since 2021/10/15
 */
@RestControllerAdvice
public class ResponseBodyHandler implements ResponseBodyAdvice<Object> {

    private final List<String> basePackages;
    private final Map<Method, Boolean> methodCache = new ConcurrentHashMap<>();

    public ResponseBodyHandler(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodCache.computeIfAbsent(returnType.getMethod(), this::shouldApply);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseDTO) {
            return body;
        }
        if (body == null) {
            return ResponseDTO.success();
        } else {
            return ResponseDTO.success(body);
        }
    }

    protected boolean shouldApply(Method targetMethod) {
        if (targetMethod == null) {
            return false;
        }

        ResponseWrapOption responseWrapOption = getAnnotation(targetMethod, ResponseWrapOption.class);
        if (responseWrapOption == null) {
            responseWrapOption = getAnnotation(targetMethod.getDeclaringClass(), ResponseWrapOption.class);
        }

        if (responseWrapOption != null) {
            return !responseWrapOption.ignore();
        }

        String packageName = targetMethod.getDeclaringClass().getPackage().getName();
        return basePackages.stream().anyMatch(packageName::startsWith);
    }

}
