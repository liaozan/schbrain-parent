package com.schbrain.common.web.servlet;

import cn.hutool.core.text.CharPool;
import com.schbrain.common.web.utils.ContentCachingServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.schbrain.common.web.utils.ContentCachingServletUtils.wrapRequestIfRequired;

/**
 * 请求日志拦截器
 */
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter implements OrderedFilter {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
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
        return CorsUtils.isPreFlightRequest(request);
    }

    protected String buildLogContent(HttpServletRequest request, long startTime, long endTime) {
        long cost = endTime - startTime;
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        String body = ContentCachingServletUtils.getRequestBody(request, false);
        StringBuilder builder = new StringBuilder();
        builder.append("requestUri: ").append(method).append(CharPool.SPACE).append(requestUri);
        if (StringUtils.isNotBlank(queryString)) {
            builder.append(", queryString: ").append(queryString);
        }
        if (StringUtils.isNotBlank(body)) {
            builder.append(", body: ").append(body);
        }
        builder.append(", startTime: ").append(startTime);
        builder.append(", endTime: ").append(endTime);
        builder.append(", cost: ").append(cost).append("ms");
        return builder.toString();
    }

}
