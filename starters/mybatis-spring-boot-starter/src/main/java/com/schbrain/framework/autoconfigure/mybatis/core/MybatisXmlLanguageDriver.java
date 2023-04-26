package com.schbrain.framework.autoconfigure.mybatis.core;

import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * @author liaozan
 * @since 2021/11/27
 */
public class MybatisXmlLanguageDriver extends MybatisXMLLanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
        return new LogicDeleteSupportSqlSource(sqlSource);
    }

}