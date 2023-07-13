package com.schbrain.common.web.utils;

import cn.hutool.extra.servlet.ServletUtil;
import com.schbrain.common.exception.BaseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liaozan
 * @since 2022/1/6
 */
public class ServletUtils extends ServletUtil {

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = getRequestAttributes();
        if (requestAttributes == null) {
            throw new BaseException("No HttpServletRequest available");
        }
        return requestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = getRequestAttributes();
        if (requestAttributes == null) {
            throw new BaseException("No HttpServletResponse available");
        }
        return requestAttributes.getResponse();
    }

    @Nullable
    public static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

}
