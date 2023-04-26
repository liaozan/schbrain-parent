package com.schbrain.common.util;

import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.Maps;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liaozan
 * @since 2022/5/18
 */
public class ParameterDiscoverUtils {

    private static final ParameterNameDiscoverer DISCOVERER = new DefaultParameterNameDiscoverer();

    public static Map<String, Object> getMethodArgsMap(Method method, Object[] args) {
        String[] parameterNames = DISCOVERER.getParameterNames(method);
        if (ArrayUtil.isEmpty(parameterNames)) {
            // Return a new instance to avoid external modification causing errors
            return new LinkedHashMap<>();
        }
        Map<String, Object> argsMap = Maps.newLinkedHashMapWithExpectedSize(parameterNames.length);
        for (int i = 0; i < parameterNames.length; i++) {
            argsMap.put(parameterNames[i], args[i]);
        }
        return argsMap;
    }

}