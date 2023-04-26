package com.schbrain.framework.autoconfigure.mybatis.util;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.schbrain.framework.autoconfigure.mybatis.base.BaseEntityWithLogicDelete;
import org.springframework.util.ClassUtils;

/**
 * @author liaozan
 * @since 2022/8/27
 */
public class SqlUtils {

    public static String withLogicDeleteVersionIfNecessary(TableInfo tableInfo, String logicDeleteSql) {
        Class<?> entityType = tableInfo.getEntityType();
        if (ClassUtils.isAssignable(BaseEntityWithLogicDelete.class, entityType)) {
            logicDeleteSql = logicDeleteSql + ", delete_version=#{deleteVersion}";
        }
        return logicDeleteSql;
    }

}