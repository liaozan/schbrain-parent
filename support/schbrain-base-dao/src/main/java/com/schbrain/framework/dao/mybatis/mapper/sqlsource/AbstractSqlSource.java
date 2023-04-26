package com.schbrain.framework.dao.mybatis.mapper.sqlsource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.session.Configuration;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description
 *
 * @author liwu on 2020/1/2
 */
public abstract class AbstractSqlSource implements SqlSource {

    private static final String GENERIC_PARAM_NAME = "param";
    private static final Pattern pattern = Pattern.compile("#\\{.+?}");
    protected final Configuration configuration;

    protected AbstractSqlSource(Configuration configuration) {
        this.configuration = configuration;
    }

    protected String getGenericParamSqlAndCheckParamCount(Object[] args) {
        String sql = (String) args[0];
        if (StringUtils.isBlank(sql)) {
            return "1=1";
        }
        Object[] params = ((Object[]) args[1]);
        int paramCount = null == params ? 0 : params.length;
        Matcher matcher = pattern.matcher(sql);
        int i = 0;
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(result, "#{" + GENERIC_PARAM_NAME + i++ + "}");
        }
        if (i != paramCount) {
            throw new IllegalArgumentException("The number of params is not the same as placeholders");
        }
        if (paramCount == 0) {
            return sql;
        }
        return matcher.appendTail(result).toString();
    }

    protected Map<String, Object> getSqlCommandParam(Object[] args) {
        final Map<String, Object> param = new ParamMap<>();
        if (null == args || args.length == 0) {
            return param;
        }
        int i = 0;
        for (Object arg : args) {
            param.put(GENERIC_PARAM_NAME + i++, arg);
        }
        return param;
    }

    protected BoundSql getBoundSql(String sql, Object parameterObject) {
        Object[] args = (Object[]) ((Map<?, ?>) parameterObject).get("array");
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        Map<String, Object> paramMap = getSqlCommandParam((Object[]) args[1]);
        String whereClause = replacePlaceholder(getGenericParamSqlAndCheckParamCount(args));
        SqlSource sqlSource = sqlSourceParser.parse(sql + whereClause, Map.class, paramMap);
        BoundSql boundSql = sqlSource.getBoundSql(paramMap);
        paramMap.forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }

    protected String replacePlaceholder(String sql) {
        return PropertyParser.parse(sql, configuration.getVariables());
    }

}