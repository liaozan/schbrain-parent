package com.schbrain.common.web.argument;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schbrain.common.util.JacksonUtils;
import com.schbrain.common.web.annotation.BodyParam;
import lombok.Setter;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author liaozan
 * @since 2022-12-02
 */
@Setter
public class BodyParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    private static final String METHOD_BODY_CACHE_KEY = BodyParamMethodArgumentResolver.class.getName() + ".bodyParamCache";

    private ObjectMapper objectMapper;

    public BodyParamMethodArgumentResolver() {
        this(JacksonUtils.getObjectMapper());
    }

    public BodyParamMethodArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BodyParam.class);
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        BodyParam annotation = parameter.getParameterAnnotation(BodyParam.class);
        Assert.notNull(annotation, "annotation should never be null");
        return new NamedValueInfo(annotation.name(), annotation.required(), annotation.defaultValue());
    }

    @Override
    @Nullable
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        JsonNode paramNode = getParamNode(request);
        JsonNode parameterValue = paramNode.get(name);
        if (parameterValue == null || parameterValue.isNull()) {
            return null;
        }
        Class<?> parameterType = parameter.getParameterType();
        return objectMapper.convertValue(parameterValue, parameterType);
    }

    private JsonNode getParamNode(NativeWebRequest nativeWebRequest) throws IOException {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(request != null, "request must not be null");
        JsonNode paramNode = (JsonNode) request.getAttribute(METHOD_BODY_CACHE_KEY);
        if (paramNode == null) {
            InputStream inputStream = StreamUtils.nonClosing(request.getInputStream());
            paramNode = objectMapper.readTree(inputStream);
            request.setAttribute(METHOD_BODY_CACHE_KEY, paramNode);
        }
        return paramNode;
    }

}