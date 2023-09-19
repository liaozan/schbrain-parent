package com.schbrain.common.web.support.authentication;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import com.schbrain.common.util.JacksonUtils;
import com.schbrain.common.util.ValidateUtils;
import com.schbrain.common.web.result.ResponseDTO;
import com.schbrain.common.web.support.BaseHandlerInterceptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

import static com.schbrain.common.constants.ResponseActionConstants.ALERT;
import static com.schbrain.common.constants.ResponseCodeConstants.LOGIN_REQUIRED;

/**
 * @author liaozan
 * @since 2022/11/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthenticationInterceptor extends BaseHandlerInterceptor implements Ordered {

    private Authenticator authenticator;

    public AuthenticationInterceptor(Authenticator authenticator) {
        ValidateUtils.notNull(authenticator, "authenticator must not be null");
        this.authenticator = authenticator;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        if (authenticator.validate(request, response, handlerMethod)) {
            return true;
        }
        writeResult(response, buildAccessDeniedResponse());
        return false;
    }

    @Override
    protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception ex) {
        authenticator.afterCompletion(request, response, handlerMethod, ex);
    }

    protected void writeResult(HttpServletResponse response, ResponseDTO<?> result) {
        String resultString = JacksonUtils.toJsonString(result);
        ServletUtil.write(response, resultString, ContentType.JSON.toString(StandardCharsets.UTF_8));
    }

    protected <T> ResponseDTO<T> buildAccessDeniedResponse() {
        return ResponseDTO.error("未获取到认证信息, 请尝试重新登录", LOGIN_REQUIRED, ALERT);
    }

}
