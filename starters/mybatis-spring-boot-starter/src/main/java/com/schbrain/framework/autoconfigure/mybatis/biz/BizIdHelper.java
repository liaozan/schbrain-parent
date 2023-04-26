package com.schbrain.framework.autoconfigure.mybatis.biz;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.schbrain.framework.autoconfigure.mybatis.annotation.BizId;
import com.schbrain.framework.autoconfigure.mybatis.core.BizIdColumnField;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        Configuration configuration = tableInfo.getConfiguration();
        if (configuration.isMapUnderscoreToCamelCase()) {
            return StringUtils.camelToUnderline(bizIdField.getName());
        }
        return bizIdField.getName();
    }

}