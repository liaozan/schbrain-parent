package com.schbrain.framework.autoconfigure.mybatis.sql.method;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.schbrain.framework.autoconfigure.mybatis.util.SqlUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * Mybatis-Plus 对于 DeleteById 的逻辑删除和其他 Delete 行为不一致, 所以这里去掉了不一致的行为
 * <a href="https://github.com/baomidou/mybatis-plus/issues/4781">详情见 Github Issue</a>
 *
 * @author liaozan
 * @since 2021/11/26
 */
public class DeleteById extends com.baomidou.mybatisplus.core.injector.methods.DeleteById {

    private static final long serialVersionUID = 998500455669716402L;

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        if (tableInfo.isWithLogicDelete()) {
            return addLogicDeleteMappedStatement(mapperClass, modelClass, tableInfo);
        } else {
            return addDeleteMappedStatement(mapperClass, tableInfo);
        }
    }

    @Override
    protected String sqlLogicSet(TableInfo table) {
        String logicSet = super.sqlLogicSet(table);
        return SqlUtils.withLogicDeleteVersionIfNecessary(table, logicSet);
    }

    private MappedStatement addDeleteMappedStatement(Class<?> mapperClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BY_ID;
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Object.class);
        return this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    private MappedStatement addLogicDeleteMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE_BY_ID;
        String sql = getLogicDeleteSql(tableInfo, sqlMethod);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Object.class);
        return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    private String getLogicDeleteSql(TableInfo tableInfo, SqlMethod sqlMethod) {
        String tableName = tableInfo.getTableName();
        String keyColumn = tableInfo.getKeyColumn();
        String keyProperty = tableInfo.getKeyProperty();
        String logicDeleteSql = tableInfo.getLogicDeleteSql(true, true);
        return String.format(sqlMethod.getSql(), tableName, sqlLogicSet(tableInfo), keyColumn, keyProperty, logicDeleteSql);
    }

}