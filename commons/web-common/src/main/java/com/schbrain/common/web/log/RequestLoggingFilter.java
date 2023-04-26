package com.schbrain.common.web.log;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import com.schbrain.common.web.properties.WebProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 请求日志拦截器
 */
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter implements OrderedFilter {

    private final WebProperties webProperties;

    public RequestLoggingFilter(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (shouldSkip(request)) {
            chain.doFilter(request, response);
            return;
        }

        request = wrapRequestIfRequired(request);
        long startTime = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info(buildLogContent(request, startTime, endTime));
        }
    }

    protected boolean shouldSkip(HttpServletRequest request) {
        if (!webProperties.isEnableRequestLogging()) {
            return true;
        }
        return CorsUtils.isPreFlightRequest(request);
    }

    protected String buildLogContent(HttpServletRequest request, long startTime, long endTime) {
        long cost = endTime - startTime;
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        String body = getRequestBody(request);
        StringBuilder builder = new StringBuilder();
        builder.append("requestUri: ").append(method).append(StrPool.C_SPACE).append(requestUri);
        if (StringUtils.hasText(queryString)) {
            builder.append(", queryString: ").append(queryString);
        }
        if (StringUtils.hasText(body)) {
            builder.append(", body: ").append(body);
        }
        builder.append(", startTime: ").append(startTime);
        builder.append(", endTime: ").append(endTime);
        builder.append(", cost: ").append(cost).append("ms");
        return builder.toString();
    }

    protected String getRequestBody(HttpServletRequest request) {
        ContentCachingRequestWrapper nativeRequest = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (nativeRequest == null) {
            return null;
        }
        byte[] content = nativeRequest.getContentAsByteArray();
        if (ArrayUtil.isEmpty(content)) {
            return null;
        }
        String charset = nativeRequest.getCharacterEncoding();
        try {
            return new String(content, charset);
        } catch (UnsupportedEncodingException e) {
            log.warn("unsupported charset {} detect during convert body to String", charset, e);
            return null;
        }
    }

    protected HttpServletRequest wrapRequestIfRequired(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

}