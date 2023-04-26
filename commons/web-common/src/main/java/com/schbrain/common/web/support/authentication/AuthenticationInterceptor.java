package com.schbrain.common.web.support.authentication;

import cn.hutool.extra.servlet.ServletUtil;
import com.schbrain.common.util.JacksonUtils;
import com.schbrain.common.web.result.ResponseDTO;
import com.schbrain.common.web.support.BaseHandlerInterceptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.schbrain.common.constants.ResponseActionConstants.ALERT;
import static com.schbrain.common.constants.ResponseCodeConstants.LOGIN_REQUIRED;

/**
 * @author liaozan
 * @since 2022/11/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthenticationInterceptor extends BaseHandlerInterceptor {

    private Authenticator authenticator;

    public AuthenticationInterceptor(Authenticator authenticator) {
        Assert.notNull(authenticator, "authenticator must not be null");
        this.authenticator = authenticator;
    }

    @Override
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        boolean validated = authenticator.validate(request, response, handler);
        if (validated) {
            return true;
        }
        writeResult(response, buildAccessDeniedResponse());
        return false;
    }

    @Override
    protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Exception ex) {
        authenticator.afterCompletion(request, response, handler, ex);
    }

    protected void writeResult(HttpServletResponse response, ResponseDTO<?> result) {
        String resultString = JacksonUtils.toJsonString(result);
        ServletUtil.write(response, resultString, MediaType.APPLICATION_JSON_VALUE);
    }

    protected <T> ResponseDTO<T> buildAccessDeniedResponse() {
        return ResponseDTO.error("未获取到认证信息, 请尝试重新登录", LOGIN_REQUIRED, ALERT);
    }

}