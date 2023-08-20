package com.schbrain.common.web.servlet;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.schbrain.common.web.utils.ContentCachingServletUtils.wrapRequestIfRequired;

/**
 * @author liaozan
 * @since 2023/8/20
 */
public class RequestWrapperFilter extends OncePerRequestFilter implements OrderedFilter {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(wrapRequestIfRequired(request), response);
    }

}
