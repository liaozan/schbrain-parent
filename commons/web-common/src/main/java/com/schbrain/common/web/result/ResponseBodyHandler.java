package com.schbrain.common.web.result;

import com.schbrain.common.web.annotation.ResponseWrapOption;
import com.schbrain.common.web.properties.WebProperties;
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

/**
 * @author liaozan
 * @since 2021/10/15
 */
@RestControllerAdvice
public class ResponseBodyHandler implements ResponseBodyAdvice<Object> {

    private final WebProperties webProperties;
    private final List<String> basePackages;
    private final Map<Method, Boolean> methodCache;

    public ResponseBodyHandler(WebProperties webProperties, List<String> basePackages) {
        this.webProperties = webProperties;
        this.basePackages = basePackages;
        this.methodCache = new ConcurrentHashMap<>();
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (!webProperties.isWrapResponse()) {
            return false;
        }
        return methodCache.computeIfAbsent(returnType.getMethod(), this::shouldApply);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        return beforeBodyWrite(body);
    }

    protected Object beforeBodyWrite(Object body) {
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

        Class<?> declaringClass = targetMethod.getDeclaringClass();

        ResponseWrapOption responseWrapOption = targetMethod.getAnnotation(ResponseWrapOption.class);
        if (responseWrapOption == null) {
            responseWrapOption = declaringClass.getAnnotation(ResponseWrapOption.class);
        }

        if (responseWrapOption != null) {
            return !responseWrapOption.ignore();
        }

        String packageName = declaringClass.getPackage().getName();
        return basePackages.stream().anyMatch(packageName::startsWith);
    }

}