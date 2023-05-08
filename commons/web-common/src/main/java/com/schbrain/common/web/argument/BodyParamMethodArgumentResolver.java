package com.schbrain.common.web.argument;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schbrain.common.util.JacksonUtils;
import com.schbrain.common.web.annotation.BodyParam;
import lombok.Setter;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.schbrain.common.web.utils.ContentCachingServletUtils.wrapRequestIfRequired;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * @author liaozan
 * @since 2022-12-02
 */
@Setter
public class BodyParamMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

    private static final String REQUEST_BODY_CACHE = BodyParamMethodArgumentResolver.class.getName() + ".requestBodyCache";

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
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        JsonNode requestBody = getRequestBody(request);
        JsonNode parameterValue = requestBody.get(name);
        if (parameterValue == null || parameterValue.isNull()) {
            return null;
        }
        Class<?> parameterType = parameter.getParameterType();
        return objectMapper.convertValue(parameterValue, parameterType);
    }

    private JsonNode getRequestBody(NativeWebRequest nativeWebRequest) throws IOException {
        JsonNode requestBody = (JsonNode) nativeWebRequest.getAttribute(REQUEST_BODY_CACHE, SCOPE_REQUEST);
        if (requestBody == null) {
            HttpServletRequest request = wrapRequestIfRequired(nativeWebRequest.getNativeRequest(HttpServletRequest.class));
            requestBody = objectMapper.readTree(request.getInputStream());
            nativeWebRequest.setAttribute(REQUEST_BODY_CACHE, requestBody, SCOPE_REQUEST);
        }
        return requestBody;
    }

}