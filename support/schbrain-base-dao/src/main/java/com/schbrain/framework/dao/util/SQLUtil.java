package com.schbrain.framework.dao.util;

import com.schbrain.common.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * description
 *
 * @author liwu on 2019/8/19
 */
public class SQLUtil {

    private static final Set<Class<?>> NUMBER_TYPES = Set.of(Integer.class, Long.class, Short.class, Byte.class, Double.class, Float.class);

    public static <T> String buildInClause(String columnName, Class<T> valueType, List<T> values) {
        Set<T> valueList = StreamUtils.filterToSet(values, Objects::nonNull);
        if (CollectionUtils.isEmpty(valueList)) {
            throw new IllegalArgumentException("Value list can not be empty.");
        }
        StringBuilder builder = new StringBuilder(columnName).append(" in (");
        if (isNumberType(valueType)) {
            builder.append(StreamUtils.join(valueList));
        } else {
            builder.append(StreamUtils.join(valueList, SQLUtil::escapeSql));
        }
        builder.append(")");
        return StringUtils.wrap(builder.toString(), " ");
    }

    private static String escapeSql(Object value) {
        if (value instanceof String) {
            return StringUtils.wrap((String) value, "'");
        } else {
            return String.valueOf(value);
        }
    }

    private static boolean isNumberType(Class<?> valueType) {
        return NUMBER_TYPES.contains(valueType);
    }

}
