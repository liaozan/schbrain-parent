package com.schbrain.common.util.support.trace;

import com.schbrain.common.util.JacksonUtils;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.Map;

import static com.schbrain.common.util.ParameterDiscoverUtils.getMethodArgsMap;

/**
 * Please use it only for method
 * <p>
 * Trace the parameter of method which annotated with this annotation
 *
 * @author liaozan
 * @since 2022/3/31
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceParam {

    FormatShape shape() default FormatShape.PRETTY_JSON;

    enum FormatShape {

        RAW {
            @Override
            protected String format0(Map<String, Object> argsMap) {
                return argsMap.toString();
            }
        },

        PRETTY_JSON {
            @Override
            public String format0(Map<String, Object> argsMap) {
                return JacksonUtils.toPrettyJsonString(argsMap);
            }
        };

        public String format(Method method, Object[] args) {
            Map<String, Object> methodArgsMap = getMethodArgsMap(method, args);
            return format0(methodArgsMap);
        }

        protected abstract String format0(Map<String, Object> argsMap);
    }

}