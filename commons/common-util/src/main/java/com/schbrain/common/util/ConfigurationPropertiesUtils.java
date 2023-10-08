package com.schbrain.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
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

    public static Map<String, Object> toMap(Object source, boolean ignoreNullValue) {
        return toMap(source, true, DEFAULT_CONVERTER);
    }

    public static Map<String, Object> toMap(Object source, boolean ignoreNullValue, Converter<String, String> converter) {
        if (source == null) {
            return Collections.emptyMap();
        }

        Class<?> sourceClass = source.getClass();
        if (sourceClass.isAnnotationPresent(ConfigurationProperties.class)) {
            String prefix = getPrefix(sourceClass);
            Map<String, Object> sourceMap = new LinkedHashMap<>();
            CopyOptions copyOptions = CopyOptions.create()
                    .setIgnoreNullValue(ignoreNullValue)
                    .setPropertiesFilter((field, value) -> !field.isAnnotationPresent(NestedConfigurationProperty.class))
                    .setFieldNameEditor(key -> prefix + "." + converter.convert(key));
            return BeanUtil.beanToMap(source, sourceMap, copyOptions);
        }
        return BeanUtil.beanToMap(source);
    }

    public static String getPrefix(Class<?> sourceClass) {
        ConfigurationProperties annotation = sourceClass.getAnnotation(ConfigurationProperties.class);
        if (annotation == null) {
            String className = ConfigurationProperties.class.getName();
            String errorDetail = sourceClass.getSimpleName() + " must annotated @" + className + " or overwrite getPropertiesPrefix method";
            throw new IllegalStateException(errorDetail);
        }
        MergedAnnotation<ConfigurationProperties> mergedAnnotation = MergedAnnotation.from(annotation);
        return mergedAnnotation.getString(MergedAnnotation.VALUE);
    }

}
