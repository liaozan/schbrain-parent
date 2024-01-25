package com.schbrain.common.web.servlet;

import cn.hutool.core.text.CharPool;
import com.schbrain.common.web.support.BaseOncePerRequestFilter;
import com.schbrain.common.web.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.schbrain.common.web.utils.RequestContentCachingUtils.getRequestBody;
import static com.schbrain.common.web.utils.RequestContentCachingUtils.wrapIfRequired;

/**
 * @author liaozan
 * @since 2023/11/15
 */
@Slf4j
public class RequestLoggingFilter extends BaseOncePerRequestFilter {

    @Override
    protected void filterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        request = wrapIfRequired(request);
        long startTime = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long endTime = System.currentTimeMillis();
            log.info(buildLogContent(request, startTime, endTime));
        }
    }

    protected String buildLogContent(HttpServletRequest request, long startTime, long endTime) {
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        String requestBody = getRequestBody(request);
        StringBuilder builder = new StringBuilder();
        builder.append("requestUri: ").append(method).append(CharPool.SPACE).append(requestUri);
        if (StringUtils.isNotBlank(queryString)) {
            builder.append(", query: ").append(queryString);
        }
        if (StringUtils.isNotBlank(requestBody)) {
            builder.append(", body: ").append(requestBody);
        }
        builder.append(", clientIp: ").append(ServletUtils.getClientIP(request));
        builder.append(", start: ").append(startTime);
        builder.append(", end: ").append(endTime);
        builder.append(", cost: ").append(endTime - startTime).append("ms");
        return builder.toString();
    }

}
