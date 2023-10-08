package com.schbrain.framework.autoconfigure.mybatis.biz;

import com.baomidou.mybatisplus.core.metadata.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.schbrain.common.exception.BaseException;
import com.schbrain.framework.autoconfigure.mybatis.annotation.BizId;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author liaozan
 * @since 2023-04-17
 */
public class BizIdHelper {

    private static final Map<Class<?>, BizIdColumnField> BIZ_ID_COLUMN_CACHE = new ConcurrentHashMap<>();

    public static BizIdColumnField getBizColumnField(Class<?> entityClass) {
        if (entityClass == null) {
            return null;
        }
        return BIZ_ID_COLUMN_CACHE.get(entityClass);
    }

    public static void putBizColumnField(Class<?> entityClass, BizIdColumnField bizIdColumnField) {
        if (entityClass == null) {
            return;
        }
        BIZ_ID_COLUMN_CACHE.put(entityClass, bizIdColumnField);
    }

    public static String getColumnName(Class<?> entityClass, Field bizIdField, BizId annotation) {
        if (StringUtils.isNotBlank(annotation.value())) {
            return annotation.value();
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        return getBizIdFieldInfo(tableInfo, bizIdField).getColumn();
    }

    private static TableFieldInfo getBizIdFieldInfo(TableInfo tableInfo, Field bizIdField) {
        List<TableFieldInfo> fieldInfoList = tableInfo.getFieldList().stream()
                .filter(fieldInfo -> fieldInfo.getField().equals(bizIdField))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fieldInfoList)) {
            throw new BaseException(String.format("%s can not be found in fieldList", bizIdField.getName()));
        }
        return fieldInfoList.get(0);
    }

}
