package com.schbrain.framework.dao.mybatis.mapper.sqlsource;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

/**
 * description
 *
 * @author liwu on 2020/1/2
 */
public class DeleteByConditionSqlSource extends AbstractSqlSource {

    private final String deleteClause;

    public DeleteByConditionSqlSource(Configuration configuration, String tableName) {
        super(configuration);
        this.deleteClause = "delete from " + tableName + " where ";
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return getBoundSql(deleteClause, parameterObject);
    }

}