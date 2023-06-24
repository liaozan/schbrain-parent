package com.schbrain.common.web.support.authentication;

import cn.hutool.extra.spring.SpringUtil;
import com.schbrain.common.annotation.IgnoreLogin;
import com.schbrain.common.web.properties.WebProperties;
import com.schbrain.common.web.utils.HandlerMethodAnnotationUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liaozan
 * @since 2022/11/12
 */
@Data
@Slf4j
public abstract class AbstractAuthenticator implements Authenticator {

    private final String authenticationVariableName;

    public AbstractAuthenticator() {
        this(SpringUtil.getBean(WebProperties.class).getAuthenticationVariableName());
    }

    public AbstractAuthenticator(String authenticationVariableName) {
        Assert.hasText(authenticationVariableName, "authenticationVariableName must not be empty");
        this.authenticationVariableName = authenticationVariableName;
    }

    @Override
    public boolean validate(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        boolean ignore = HandlerMethodAnnotationUtils.hasAnnotation(handlerMethod, IgnoreLogin.class);
        if (ignore) {
            return true;
        }
        String authentication = getAuthentication(request);
        if (StringUtils.isBlank(authentication)) {
            return false;
        }
        return doValidate(authentication, request, response, handlerMethod);
    }

    protected abstract boolean doValidate(String authentication, HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod);

    @Nullable
    protected String getAuthentication(HttpServletRequest request) {
        String authentication = request.getHeader(authenticationVariableName);
        if (StringUtils.isBlank(authentication)) {
            authentication = request.getParameter(authenticationVariableName);
        }
        if (StringUtils.isBlank(authentication)) {
            log.warn("Can not get authentication from request, authenticationVariableName: {}", authenticationVariableName);
        }
        return authentication;
    }

}