package com.schbrain.common.web.support.converter.datetime;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liaozan
 * @since 2023/9/16
 */
abstract class DateTimeConditionalConverter<T> implements ConditionalGenericConverter {

    private final TypeDescriptor targetType;
    private final TypeDescriptor stringType = TypeDescriptor.valueOf(String.class);
    private final Map<String, DateTimeFormatter> formatters = new ConcurrentHashMap<>();

    DateTimeConditionalConverter() {
        this.targetType = TypeDescriptor.valueOf(ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).getRawClass());
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.isAssignableTo(stringType) && targetType.isAssignableTo(this.targetType);
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(String.class, targetType.getObjectType()));
    }

    @Override
    public T convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        return convert((String) source, targetType);
    }

    protected T convert(String source, TypeDescriptor targetType) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        if (NumberUtil.isLong(source)) {
            return doConvert(Long.parseLong(source));
        } else {
            DateTimeFormatter formatter = ofPattern(determinePattern(targetType));
            return doConvert(source, formatter);
        }
    }

    protected String determinePattern(TypeDescriptor targetType) {
        DateTimeFormat annotation = targetType.getAnnotation(DateTimeFormat.class);
        String pattern = null;
        if (annotation != null) {
            pattern = annotation.pattern();
        }
        if (StringUtils.isBlank(pattern)) {
            pattern = defaultPattern();
        }
        return pattern;
    }

    protected DateTimeFormatter ofPattern(String pattern) {
        return formatters.computeIfAbsent(pattern, ignore -> DatePattern.createFormatter(pattern));
    }

    protected abstract String defaultPattern();

    protected abstract T doConvert(Long source);

    protected abstract T doConvert(String source, DateTimeFormatter formatter);

}
