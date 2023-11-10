package com.schbrain.framework.dao.mybatis.mapper.sqlsource;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

/**
 * description
 *
 * @author liwu on 2020/1/2
 */
public class UpdateByConditionSqlSource extends AbstractSqlSource {

    private final String scriptSql;

    private final LanguageDriver languageDriver;

    public UpdateByConditionSqlSource(Configuration configuration, LanguageDriver languageDriver, String scriptSql) {
        super(configuration);
        this.languageDriver = languageDriver;
        this.scriptSql = scriptSql;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return getBoundSql(scriptSql, parameterObject);
    }

    @Override
    protected BoundSql getBoundSql(String scriptSql, Object parameterObject) {
        Object[] args = (Object[]) ((Map<?, ?>) parameterObject).get("array");
        Object[] conditionArgs = new Object[]{args[1], args[2]};
        Map<String, Object> paramMap = getSqlCommandParam((Object[]) args[2]);
        paramMap.put("obj", args[0]);
        String whereClause = replacePlaceholder(getGenericParamSqlAndCheckParamCount(conditionArgs));
        String sql = scriptSql + " where " + whereClause + "</script>";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        BoundSql boundSql = sqlSource.getBoundSql(paramMap);
        paramMap.forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }

}
