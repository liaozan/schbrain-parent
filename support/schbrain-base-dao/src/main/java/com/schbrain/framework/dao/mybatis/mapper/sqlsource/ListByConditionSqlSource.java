package com.schbrain.framework.dao.mybatis.mapper.sqlsource;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

/**
 * description
 *
 * @author liwu on 2020/1/2
 */
public class ListByConditionSqlSource extends AbstractSqlSource {

    private final String selectClause;

    public ListByConditionSqlSource(Configuration configuration, String selectClause) {
        super(configuration);
        this.selectClause = selectClause + " where ";
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return getBoundSql(selectClause, parameterObject);
    }

}