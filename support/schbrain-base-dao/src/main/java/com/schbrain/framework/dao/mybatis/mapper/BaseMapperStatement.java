package com.schbrain.framework.dao.mybatis.mapper;

import cn.hutool.crypto.SecureUtil;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.schbrain.framework.dao.mybatis.mapper.sqlsource.*;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * description
 *
 * @author liwu on 2019/8/2
 */
public class BaseMapperStatement {

    private static final String GMT_UPDATE_FIELD = "gmtUpdate";

    private static final String GMT_CREATE_FIELD = "gmtCreate";

    private final Configuration configuration;

    private final Class<?> mapperInterface;

    private final LanguageDriver languageDriver;

    private final Map<String, String> fieldColumnMap = new LinkedHashMap<>();

    private final String tableName;

    private final Class<?> domainClass;

    private final Field[] fields;

    private final List<ResultMap> objectResultMapList = new ArrayList<>(1);

    private final List<ResultMap> intResultMapList = new ArrayList<>(1);

    private String selectClause;

    private String insertClause;

    public BaseMapperStatement(Configuration configuration, Class<?> mapperInterface, Class<?> domainClass, String tableName, Field[] fields) {
        this.configuration = configuration;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        this.mapperInterface = mapperInterface;
        this.tableName = tableName;
        this.domainClass = domainClass;
        this.fields = fields;
        this.objectResultMapList.add(new ResultMap.Builder(configuration, "objectResultMap", domainClass, Collections.emptyList()).build());
        this.intResultMapList.add(new ResultMap.Builder(configuration, "intResult", int.class, Collections.emptyList()).build());
        setClause();
        addAddMS();
        addGetByIdMS();
        addListByIdListMS();
        addListByConditionMS();
        addListByObjectMS();
        addListOneByObjectMS();
        addCountByConditionMS();
        addDeleteByIdMS();
        addDeleteByIdListMS();
        addDeleteByConditionMS();
        addUpdateByIdMS();
        addUpdateByIdWithNullMS();
        addUpdateByConditionMS();
        addUpdateByCompleteSqlMS();
    }

    public String getAddMSId() {
        String methodName = "add";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getAddListMSId(String... fields) {
        String sql = getInsertListScript(fields);
        String msId = mapperInterface.getName() + ".addList" + SecureUtil.md5(sql);
        if (!configuration.hasStatement(msId)) {
            addAddListMS(msId, sql);
        }
        return msId;
    }

    public String getGetByIdMSId() {
        String methodName = "getById";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getListByIdListMSId() {
        String methodName = "getByIdList";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getListByConditionMSId() {
        String methodName = "listByCondition";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getListByObjectMSId() {
        String methodName = "listByObject";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getGetOneByObjectMSId() {
        String methodName = "getOneByObject";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getCountByConditionMSId() {
        String methodName = "getCountByCondition";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getDeleteByIdMSId() {
        String methodName = "deleteById";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getDeleteByIdListMSId() {
        String methodName = "deleteByIdList";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getDeleteByConditionMSId() {
        String methodName = "deleteByCondition";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getUpdateByIdMSId() {
        String methodName = "updateById";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getUpdateByIdWithNullMSId() {
        String methodName = "updateByIdWithNull";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getUpdateByConditionMSId() {
        String methodName = "updateByCondition";
        return mapperInterface.getName() + "." + methodName;
    }

    public String getUpdateByCompleteSqlMSId() {
        String methodName = "updateByCompleteSql";
        return mapperInterface.getName() + "." + methodName;
    }

    private void setClause() {
        Converter<String, String> converter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
        StringBuilder selectClause = new StringBuilder("select ");
        StringBuilder insertClause = new StringBuilder("insert into ");
        insertClause.append(tableName).append("(");
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String fieldName = field.getName();
            String columnName = converter.convert(fieldName);
            fieldColumnMap.put(fieldName, columnName);
            selectClause.append(" ").append(columnName).append(",");
            insertClause.append(" ").append(columnName).append(",");
        }

        selectClause.replace(selectClause.length() - 1, selectClause.length(), " from ").append(tableName).append(" ");
        insertClause.replace(insertClause.length() - 1, insertClause.length(), ") values ");
        this.selectClause = selectClause.toString();
        this.insertClause = insertClause.toString();
    }

    ///////////////////add
    private void addAddMS() {
        String sql = getInsertScript();
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, domainClass);
        addUpdateMappedStatement(getAddMSId(), sqlSource, SqlCommandType.INSERT);
    }

    private String getInsertScript() {
        StringBuilder sb = new StringBuilder("<script>");
        sb.append("insert into ").append(tableName);
        sb.append("<trim prefix='(' suffix=')' suffixOverrides=','>");
        fieldColumnMap.forEach((field, column) -> {
            if (GMT_UPDATE_FIELD.equals(field) || GMT_CREATE_FIELD.equals(field)) {
                sb.append(column).append(",");
            } else {
                sb.append(String.format("<if test='%s != null'>%s,</if>", field, column));
            }
        });
        sb.append("</trim>");
        sb.append("<trim prefix='values (' suffix=')' suffixOverrides=','>");
        fieldColumnMap.forEach((field, column) -> {
            if (GMT_UPDATE_FIELD.equals(field)) {
                sb.append("<choose>");
                sb.append(String.format("<when test='%s != null'>#{%s},</when><otherwise>now(),</otherwise></choose>", GMT_UPDATE_FIELD, GMT_UPDATE_FIELD));
            } else if (GMT_CREATE_FIELD.equals(field)) {
                sb.append("<choose>");
                sb.append(String.format("<when test='%s != null'>#{%s},</when><otherwise>now(),</otherwise></choose>", GMT_CREATE_FIELD, GMT_CREATE_FIELD));
            } else {
                sb.append(String.format("<if test='%s != null'>#{%s},</if>", field, field));
            }
        });
        sb.append("</trim></script>");
        return sb.toString();
    }

    ///////////////////addList
    private void addAddListMS(String msId, String sql) {
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, List.class);
        addUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
    }

    private String getInsertListScript(String... fields) {
        String tmpInsertClause;
        Set<String> fieldSet;
        if (null == fields || 0 == fields.length) {
            tmpInsertClause = insertClause;
            fieldSet = fieldColumnMap.keySet();
        } else {
            fieldSet = new LinkedHashSet<>();
            StringBuilder sb = new StringBuilder("insert into ");
            sb.append(tableName).append("(");

            for (String field : fields) {
                if (GMT_UPDATE_FIELD.equals(field) || GMT_CREATE_FIELD.equals(field)) {
                    continue;
                }
                String column = fieldColumnMap.get(field);
                if (null == column) {
                    throw new IllegalArgumentException("Can not find column of field:" + field);
                }
                fieldSet.add(field);
                sb.append(column).append(",");
            }
            // 判断是否有gmtUpdate和gmtCreate
            String gmtUpdate = fieldColumnMap.get(GMT_UPDATE_FIELD);
            if (null != gmtUpdate) {
                fieldSet.add(GMT_UPDATE_FIELD);
                sb.append(gmtUpdate).append(",");
            }
            String gmtCreate = fieldColumnMap.get(GMT_CREATE_FIELD);
            if (null != gmtCreate) {
                fieldSet.add(GMT_CREATE_FIELD);
                sb.append(gmtCreate).append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), ") values ");
            tmpInsertClause = sb.toString();
        }
        StringBuilder sql = new StringBuilder("<script>");
        sql.append(tmpInsertClause).append("<foreach item = 'obj' collection='list' separator=','>");
        sql.append("<trim prefix='(' suffix=')' suffixOverrides=','>");
        for (String field : fieldSet) {
            if (GMT_UPDATE_FIELD.equals(field)) {
                sql.append(String.format("<choose><when test='obj.%s != null'>#{obj.%s},</when>", GMT_UPDATE_FIELD, GMT_UPDATE_FIELD)).append("<otherwise>now(),</otherwise></choose>");
            } else if (GMT_CREATE_FIELD.equals(field)) {
                sql.append(String.format("<choose><when test='obj.%s != null'>#{obj.%s},</when>", GMT_CREATE_FIELD, GMT_CREATE_FIELD)).append("<otherwise>now(),</otherwise></choose>");
            } else {
                sql.append(String.format("#{obj.%s},", field));
            }
        }
        sql.append("</trim></foreach></script>");
        return sql.toString();
    }

    ////////////////getById
    private void addGetByIdMS() {
        String sql = selectClause + " where id = ?";
        ParameterMapping pm = new ParameterMapping.Builder(configuration, "id", Long.class).build();
        StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql, Collections.singletonList(pm));
        addSelectMappedStatement(getGetByIdMSId(), sqlSource);
    }

    ///////////////////listByIdList
    private void addListByIdListMS() {
        String sql = "<script>" + selectClause
                + " where id in <foreach close=')' collection='list' item='id' open='(' separator=','>"
                + "#{id}</foreach></script>";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, List.class);
        addSelectMappedStatement(getListByIdListMSId(), sqlSource);
    }

    /////////////////listByCondition
    private void addListByConditionMS() {
        SqlSource sqlSource = new ListByConditionSqlSource(configuration, selectClause);
        addSelectMappedStatement(getListByConditionMSId(), sqlSource);
    }

    /////////////////listByObject
    private void addListByObjectMS() {
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, getListByObjectSql(false), domainClass);
        addSelectMappedStatement(getListByObjectMSId(), sqlSource);
    }

    private String getListByObjectSql(boolean onlyOne) {
        StringBuilder sql = new StringBuilder("<script>");
        sql.append(selectClause).append("<where>");
        fieldColumnMap.forEach((field, column) -> sql.append(String.format("<if test='%s != null'> and %s=#{%s} </if>", field, column, field)));
        sql.append("</where>");
        if (onlyOne) {
            sql.append("limit 1");
        }
        sql.append("</script>");
        return sql.toString();
    }

    /////////////////getOneByObject
    private void addListOneByObjectMS() {
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, getListByObjectSql(true), domainClass);
        addSelectMappedStatement(getGetOneByObjectMSId(), sqlSource);
    }

    /////////////////getCountByCondition
    private void addCountByConditionMS() {
        SqlSource sqlSource = new CountByConditionSqlSource(configuration, tableName);
        addCountMappedStatement(getCountByConditionMSId(), sqlSource);
    }

    ///////////////////deleteById
    private void addDeleteByIdMS() {
        String sql = "delete from " + tableName + " where id = ?";
        ParameterMapping pm = new ParameterMapping.Builder(configuration, "id", Long.class).build();
        StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql, Collections.singletonList(pm));
        addUpdateMappedStatement(getDeleteByIdMSId(), sqlSource, SqlCommandType.DELETE);
    }

    ///////////////////deleteByIdList
    private void addDeleteByIdListMS() {
        String sql = "<script>delete from " + tableName
                + " where id in <foreach close=')' collection='list' item='id' open='(' separator=','>"
                + "#{id}</foreach></script>";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, List.class);
        addUpdateMappedStatement(getDeleteByIdListMSId(), sqlSource, SqlCommandType.DELETE);
    }

    /////////////////deleteByCondition
    private void addDeleteByConditionMS() {
        SqlSource sqlSource = new DeleteByConditionSqlSource(configuration, tableName);
        addUpdateMappedStatement(getDeleteByConditionMSId(), sqlSource, SqlCommandType.DELETE);
    }

    /////////////////updateById
    private void addUpdateByIdMS() {
        String sql = getUpdateScript(false) + " where id = #{id}</script>";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, domainClass);
        addUpdateMappedStatement(getUpdateByIdMSId(), sqlSource, SqlCommandType.UPDATE);
    }

    private String getUpdateScript(boolean withNull) {
        StringBuilder sb = new StringBuilder("<script>");
        sb.append("update ").append(tableName).append("<set>");
        fieldColumnMap.forEach((field, column) -> {
            if (!GMT_UPDATE_FIELD.equals(field) && !"id".equals(column)) {
                if (withNull) {
                    sb.append(String.format("%s = #{%s},", column, field));
                } else {
                    sb.append(String.format("<if test='%s != null'>%s = #{%s},</if>", field, column, field));
                }
            }
        });
        if (fieldColumnMap.containsKey(GMT_UPDATE_FIELD)) {
            sb.append(String.format("<choose><when test='%s != null'>gmt_update = #{%s}</when>", GMT_UPDATE_FIELD, GMT_UPDATE_FIELD)).append("<otherwise>gmt_update = now()</otherwise></choose>");
        }
        sb.append("</set>");
        return sb.toString();
    }

    /////////////////updateByIdWithNull
    private void addUpdateByIdWithNullMS() {
        String sql = getUpdateScript(true) + " where id = #{id}</script>";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, domainClass);
        addUpdateMappedStatement(getUpdateByIdWithNullMSId(), sqlSource, SqlCommandType.UPDATE);
    }

    /////////////////updateByCondition
    private void addUpdateByConditionMS() {
        SqlSource sqlSource = new UpdateByConditionSqlSource(configuration, languageDriver, getUpdateByConditionScript());
        addUpdateMappedStatement(getUpdateByConditionMSId(), sqlSource, SqlCommandType.UPDATE);
    }

    private String getUpdateByConditionScript() {
        StringBuilder sb = new StringBuilder("<script>");
        sb.append("update ").append(tableName).append("<set>");
        fieldColumnMap.forEach((field, column) -> {
            if (!GMT_UPDATE_FIELD.equals(field) && !"id".equals(field)) {
                sb.append(String.format("<if test='obj.%s != null'>%s = #{obj.%s},</if>", field, column, field));
            }
        });
        if (fieldColumnMap.containsKey(GMT_UPDATE_FIELD)) {
            sb.append(String.format("<choose><when test='obj.%s != null'>gmt_update = #{obj.%s}</when>", GMT_UPDATE_FIELD, GMT_UPDATE_FIELD)).append("<otherwise>gmt_update = now()</otherwise></choose>");
        }
        sb.append("</set>");
        return sb.toString();
    }

    /////////////////updateByCompleteSql
    private void addUpdateByCompleteSqlMS() {
        SqlSource sqlSource = new UpdateByCompleteSqlSource(configuration);
        addUpdateMappedStatement(getUpdateByCompleteSqlMSId(), sqlSource, SqlCommandType.UPDATE);
    }

    ///////////////////////////////add statement/////////////////////
    private synchronized void addSelectMappedStatement(String msId, SqlSource sqlSource) {
        if (configuration.hasStatement(msId)) {
            return;
        }
        MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, SqlCommandType.SELECT)
                .resultMaps(objectResultMapList).flushCacheRequired(true).build();
        configuration.addMappedStatement(ms);
    }

    private synchronized void addCountMappedStatement(String msId, SqlSource sqlSource) {
        if (configuration.hasStatement(msId)) {
            return;
        }
        MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, SqlCommandType.SELECT)
                .resultMaps(intResultMapList).build();
        configuration.addMappedStatement(ms);
    }

    private synchronized void addUpdateMappedStatement(String msId, SqlSource sqlSource, SqlCommandType sqlCommandType) {
        if (configuration.hasStatement(msId)) {
            return;
        }
        MappedStatement ms;
        if (SqlCommandType.INSERT == sqlCommandType) {
            ms = new MappedStatement.Builder(configuration, msId, sqlSource, sqlCommandType)
                    .resultMaps(intResultMapList).keyGenerator(Jdbc3KeyGenerator.INSTANCE)
                    .keyColumn("id").keyProperty("id").build();
        } else {
            ms = new MappedStatement.Builder(configuration, msId, sqlSource, sqlCommandType)
                    .resultMaps(intResultMapList).build();
        }
        configuration.addMappedStatement(ms);
    }

}