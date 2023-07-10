package com.schbrain.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 注意！！！此类是基于 cglib 实现的
 * 默认的 cglib 只支持同名,同类型的属性转换,本类对此场景进行了增强,具体逻辑见 {@link DefaultConverter}
 * 另外最重要的, cglib 是浅拷贝,意味着如果是引用类型,修改源对象会导致目标对象的值也被修改,使用时请注意！！！
 *
 * @author liaozan
 * @since 2022/1/24
 */
@Slf4j
public class BeanCopyUtils {

    private static final Joiner CACHE_KEY_JOINER = Joiner.on("#");

    /**
     * copy object list
     */
    public static <Source, Target> List<Target> copyList(List<Source> sourceList, Class<Target> targetType) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>(0);
        }
        return StreamUtils.toList(sourceList, source -> copy(source, targetType), false, true);
    }

    /**
     * copy object
     */
    public static <Source, Target> Target copy(Source source, Class<Target> targetType) {
        if (source == null || targetType == null) {
            return null;
        }
        return copy(source, ReflectUtil.newInstanceIfPossible(targetType));
    }

    /**
     * copy object
     */
    public static <Source, Target> Target copy(Source source, Target target) {
        if (source == null || target == null) {
            return null;
        }
        BeanCopier copier = getCopier(source.getClass(), target.getClass());
        copier.copy(source, target, DefaultConverter.INSTANCE);
        return target;
    }

    private static BeanCopier getCopier(Class<?> sourceClass, Class<?> targetClass) {
        String cacheKey = buildCacheKey(sourceClass, targetClass);
        return Singleton.get(cacheKey, () -> BeanCopier.create(sourceClass, targetClass, true));
    }

    private static String buildCacheKey(Class<?> source, Class<?> target) {
        return CACHE_KEY_JOINER.join(source.getName(), target.getName());
    }

    /**
     * 基于 hutool 实现的转换
     *
     * @see ConverterRegistry#getConverter(Type, boolean)
     */
    private static class DefaultConverter implements Converter {

        private static final DefaultConverter INSTANCE = new DefaultConverter();

        @Override
        public Object convert(Object value, Class targetType, Object context) {
            if (value == null) {
                return null;
            }
            if (ClassUtils.isAssignableValue(targetType, value)) {
                return value;
            }
            return Convert.convertQuietly(targetType, value);
        }

    }

}
