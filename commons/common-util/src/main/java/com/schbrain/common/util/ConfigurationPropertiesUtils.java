package com.schbrain.common.util;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.MergedAnnotation;

import java.util.*;

/**
 * @author liaozan
 * @since 2022/1/11
 */
public class ConfigurationPropertiesUtils {

    private static final Converter<String, String> DEFAULT_CONVERTER = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN);

    public static Map<String, Object> toMap(Object source) {
        return toMap(source, true);
    }

    public static Map<String, Object> toMap(Object source, boolean ignoreNull) {
        return toMap(source, true, DEFAULT_CONVERTER);
    }

    public static Map<String, Object> toMap(Object source, boolean ignoreNull, Converter<String, String> converter) {
        if (source == null) {
            return Collections.emptyMap();
        }

        Class<?> sourceClass = source.getClass();
        if (sourceClass.isAnnotationPresent(ConfigurationProperties.class)) {
            String prefix = getPrefix(sourceClass);
            Map<String, Object> sourceMap = new LinkedHashMap<>();
            return BeanUtil.beanToMap(source, sourceMap, ignoreNull, key -> prefix + "." + converter.convert(key));
        }
        return BeanUtil.beanToMap(source);
    }

    public static String getPrefix(Class<?> sourceClass) {
        ConfigurationProperties configurationProperties = sourceClass.getAnnotation(ConfigurationProperties.class);
        MergedAnnotation<ConfigurationProperties> mergedAnnotation = MergedAnnotation.from(configurationProperties);
        return mergedAnnotation.getString(MergedAnnotation.VALUE);
    }

}