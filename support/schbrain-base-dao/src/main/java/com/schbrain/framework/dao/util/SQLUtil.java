package com.schbrain.framework.dao.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * description
 *
 * @author liwu on 2019/8/19
 */
public class SQLUtil {

    public static String buidInClause(String columnName, Class<?> valueType, List<?> valueList) {
        if (CollectionUtils.isEmpty(valueList)) {
            throw new IllegalArgumentException("Value list can not be empty.");
        }
        valueList = valueList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(valueList)) {
            throw new IllegalArgumentException("Value list can not be empty.");
        }
        StringBuilder sb = new StringBuilder(" ");
        sb.append(columnName).append(" in (");
        if (Integer.class.isAssignableFrom(valueType) || Long.class.isAssignableFrom(valueType) ||
                Short.class.isAssignableFrom(valueType) || Byte.class.isAssignableFrom(valueType) ||
                Double.class.isAssignableFrom(valueType) || Float.class.isAssignableFrom(valueType)) {
            sb.append(StringUtils.join(valueList, ',')).append(")");
        } else {
            valueList.forEach(e -> {
                sb.append("'").append(escapeSql(e)).append("',");
            });
            sb.deleteCharAt(sb.length() - 1).append(")");
        }
        sb.append(" ");
        return sb.toString();
    }

    private static String escapeSql(Object o) {
        if (o instanceof String) {
            return StringUtils.replace((String) o, "'", "''");
        } else {
            return String.valueOf(o);
        }
    }

}