package com.schbrain.common.util;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.base.Joiner;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.List;

/**
 * only support the same property type
 *
 * @author liaozan
 * @since 2022/1/24
 */
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
        copier.copy(source, target, null);
        return target;
    }

    private static BeanCopier getCopier(Class<?> sourceClass, Class<?> targetClass) {
        String cacheKey = buildCacheKey(sourceClass, targetClass);
        return Singleton.get(cacheKey, () -> BeanCopier.create(sourceClass, targetClass, false));
    }

    private static String buildCacheKey(Class<?> source, Class<?> target) {
        return CACHE_KEY_JOINER.join(source.getName(), target.getName());
    }

}