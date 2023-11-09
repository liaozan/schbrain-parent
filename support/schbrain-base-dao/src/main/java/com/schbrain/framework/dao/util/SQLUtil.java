package com.schbrain.framework.dao.util;

import com.schbrain.common.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * description
 *
 * @author liwu on 2019/8/19
 */
public class SQLUtil {

    public static <T> String buildInClause(String columnName, Class<T> valueType, List<T> values) {
        Set<T> valueList = StreamUtils.filterToSet(values, Objects::nonNull);
        if (CollectionUtils.isEmpty(valueList)) {
            throw new IllegalArgumentException("Value list can not be empty.");
        }
        StringBuilder builder = new StringBuilder(" ");
        builder.append(columnName).append(" in (");
        if (ClassUtils.isPrimitiveWrapper(valueType)) {
            builder.append(StreamUtils.join(valueList)).append(")");
        } else {
            builder.append(StreamUtils.join(valueList, SQLUtil::escapeSql)).append(")");
        }
        builder.append(" ");
        return builder.toString();
    }

    private static String escapeSql(Object value) {
        if (value instanceof String) {
            return StringUtils.replace((String) value, "'", "''");
        } else {
            return String.valueOf(value);
        }
    }

}
