package com.schbrain.framework.dao.mybatis.mapper.sqlsource;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

/**
 * description
 *
 * @author liwu on 2020/1/2
 */
public class CountByConditionSqlSource extends AbstractSqlSource {

    private final String selectClause;

    public CountByConditionSqlSource(Configuration configuration, String tableName) {
        super(configuration);
        this.selectClause = "select count(*) from " + tableName + " where ";
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return getBoundSql(selectClause, parameterObject);
    }

}