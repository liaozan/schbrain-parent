package com.schbrain.common.web.support.signature;

import cn.hutool.crypto.digest.DigestUtil;
import com.schbrain.common.web.support.BaseHandlerInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.hutool.core.text.StrPool.UNDERLINE;
import static com.schbrain.common.web.utils.ContentCachingServletUtils.getRequestBody;
import static com.schbrain.common.web.utils.ContentCachingServletUtils.wrapRequestIfRequired;

public abstract class AbstractSignatureValidationInterceptor<T extends SignatureContext> extends BaseHandlerInterceptor {

    private static final String SCH_APP_KEY = "Sch-App-Key";
    private static final String SCH_TIMESTAMP = "Sch-Timestamp";
    private static final String SCH_SIGNATURE = "Sch-Signature";
    private static final String SCH_EXPIRE_TIME = "Sch-Expire-Time";

    @Override
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        String appKey = request.getHeader(SCH_APP_KEY);
        String timestamp = request.getHeader(SCH_TIMESTAMP);
        String signature = request.getHeader(SCH_SIGNATURE);
        String expireTime = request.getHeader(SCH_EXPIRE_TIME);

        // 空校验
        if (StringUtils.isAnyBlank(appKey, timestamp, signature)) {
            throw new SignatureValidationException("签名参数为空！");
        }

        // 过期校验
        if (StringUtils.isNotBlank(expireTime) && System.currentTimeMillis() > Long.parseLong(expireTime)) {
            throw new SignatureValidationException("请求信息已过期！");
        }

        // 获取appSecret
        SignatureContext context = getSignatureContext(appKey);
        if (null == context || StringUtils.isBlank(context.getAppSecret())) {
            throw new SignatureValidationException();
        }

        request = wrapRequestIfRequired(request);
        // 校验签名
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        String body = getRequestBody(request, true);
        String compareSignature = signParams(requestUri, queryString, body, timestamp, appKey, context.getAppSecret());
        if (!signature.equals(compareSignature)) {
            throw new SignatureValidationException();
        }

        SignatureContextUtil.set(context);
        return true;
    }

    @Override
    protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception ex) {
        SignatureContextUtil.clear();
    }

    protected abstract T getSignatureContext(String appKey);

    protected String signParams(String requestUri, String queryString, String bodyString, String timestamp, String appKey, String appSecret) {
        StringBuilder toSign = new StringBuilder(requestUri);
        if (StringUtils.isNotBlank(queryString)) {
            toSign.append(UNDERLINE).append(queryString);
        }
        if (StringUtils.isNotBlank(bodyString)) {
            toSign.append(UNDERLINE).append(bodyString);
        }
        toSign.append(UNDERLINE).append(timestamp).append(UNDERLINE).append(appKey).append(UNDERLINE).append(appSecret);

        return DigestUtil.sha256Hex(toSign.toString());
    }

}
