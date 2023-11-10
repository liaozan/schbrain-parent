package com.schbrain.framework.dao.mybatis.mapper.sqlsource;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

/**
 * description
 *
 * @author liwu on 2020/1/2
 */
public class UpdateByCompleteSqlSource extends AbstractSqlSource {

    public UpdateByCompleteSqlSource(Configuration configuration) {
        super(configuration);
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        Object[] args = (Object[]) ((Map<?, ?>) parameterObject).get("array");
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        String sql = replacePlaceholder(getGenericParamSqlAndCheckParamCount(args));
        Map<String, Object> paramMap = getSqlCommandParam((Object[]) args[1]);
        SqlSource sqlSource = sqlSourceParser.parse(sql, Map.class, paramMap);
        BoundSql boundSql = sqlSource.getBoundSql(paramMap);
        paramMap.forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }

}
