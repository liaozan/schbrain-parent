package com.schbrain.framework.autoconfigure.mybatis.core;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

/**
 * @author liaozan
 * @since 2021/11/27
 */
public class LogicDeleteSupportSqlSource implements SqlSource {

    public static final String DELETE_VERSION = "deleteVersion";

    private final SqlSource sqlSource;

    public LogicDeleteSupportSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        if (hasDeleteVersionProperty(boundSql.getParameterMappings())) {
            boundSql.setAdditionalParameter(DELETE_VERSION, System.currentTimeMillis());
        }
        return boundSql;
    }

    private boolean hasDeleteVersionProperty(List<ParameterMapping> mappings) {
        return mappings.stream()
                .map(ParameterMapping::getProperty)
                .anyMatch(property -> property.equals(DELETE_VERSION));
    }

}