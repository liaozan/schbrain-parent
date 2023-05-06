package com.schbrain.framework.dao.mybatis.mapper;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.schbrain.framework.dao.mybatis.annotation.MapperConfig;
import com.schbrain.framework.dao.mybatis.exception.MapperParseException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 每一个Mapper实例对应一个BaseMapper实例
 *
 * @author liwu on 2019/7/31
 */
public class BaseMapper {

    private final SqlSessionTemplate sqlSession;

    private final Class<?> mapperInterface;

    private final BaseMapperStatement bms;

    private Class<?> domainClass;

    private String tableName;

    private Field[] fields;

    public BaseMapper(SqlSessionTemplate sqlSession, Class<?> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        parseMapperClass();
        bms = new BaseMapperStatement(sqlSession.getConfiguration(), mapperInterface, domainClass, tableName, fields);
    }

    public Object invokeBaseMethod(Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
            case "getTableName":
                return tableName;
            case "getById":
                return getById(args);
            case "listByIdList":
                return getByIdList(args);
            case "getCountByCondition":
                return sqlSession.selectOne(bms.getCountByConditionMSId(), args);
            case "getOneByCondition":
                String sql = (String) args[0];
                if (!StringUtils.containsIgnoreCase(sql, "limit")) {
                    args[0] += " limit 1";
                }
                return sqlSession.selectOne(bms.getListByConditionMSId(), args);
            case "listByCondition":
                return sqlSession.selectList(bms.getListByConditionMSId(), args);
            case "getOneByObject":
                return sqlSession.selectOne(bms.getGetOneByObjectMSId(), args[0]);
            case "listByObject":
                return sqlSession.selectList(bms.getListByObjectMSId(), args[0]);
            case "pageByCondition":
                return pageByCondition(args);
            case "add":
                return sqlSession.insert(bms.getAddMSId(), args[0]) > 0;
            case "addList":
                return sqlSession.insert(bms.getAddListMSId((String[]) args[1]), args[0]);
            case "deleteById":
                return sqlSession.delete(bms.getDeleteByIdMSId(), args[0]) > 0;
            case "deleteByIdList":
                return sqlSession.delete(bms.getDeleteByIdListMSId(), args[0]);
            case "deleteByCondition":
                return sqlSession.delete(bms.getDeleteByConditionMSId(), args);
            case "updateById":
                return updateById(args);
            case "updateByIdWithNull":
                return updateByIdWithNull(args);
            case "updateByCondition":
                return sqlSession.update(bms.getUpdateByConditionMSId(), args);
            case "updateByCompleteSql":
                return sqlSession.update(bms.getUpdateByCompleteSqlMSId(), args);
            default:
                return null;
        }
    }

    private void parseMapperClass() {
        MapperConfig mapperConfig = mapperInterface.getAnnotation(MapperConfig.class);
        if (null == mapperConfig) {
            throw new MapperParseException(String.format("Can not find MapperConfig annotation in mapper class %s ", mapperInterface.getName()));
        }
        tableName = mapperConfig.tableName();
        if (StringUtils.isBlank(tableName)) {
            throw new MapperParseException(String.format("Table name is blank in MapperConfig annotation in mapper class %s ", mapperInterface.getName()));
        }
        domainClass = mapperConfig.domainClass();
        if (Class.class.equals(domainClass)) {
            throw new MapperParseException(String.format("Domain class is not set in MapperConfig annotation in mapper class %s ", mapperInterface.getName()));
        }
        fields = domainClass.getDeclaredFields();
        if (fields.length < 1) {
            throw new MapperParseException(String.format("Domain class %s has no fields", domainClass.getName()));
        }
    }

    private Object getById(Object[] args) {
        if (null == args[0]) {
            throw new IllegalArgumentException("Parameter id can not be null");
        }
        return sqlSession.selectOne(bms.getGetByIdMSId(), args[0]);
    }

    private Object getByIdList(Object[] args) {
        if (null == args[0]) {
            throw new IllegalArgumentException("Parameter idList can not be null");
        }
        // noinspection unchecked
        List<Long> idList = (List<Long>) args[0];
        if (CollectionUtils.isEmpty(idList)) {
            throw new IllegalArgumentException("Parameter idList can not be empty");
        }
        return sqlSession.selectList(bms.getListByIdListMSId(), idList);
    }

    private Object pageByCondition(Object[] args) {
        Object[] conditionArgs;
        String orderBy = null;
        if (4 == args.length) {
            conditionArgs = new Object[]{args[2], args[3]};
        } else {
            conditionArgs = new Object[]{args[2], args[4]};
            orderBy = (String) args[3];
        }
        if (StringUtils.isNotBlank(orderBy)) {
            PageHelper.startPage((int) args[0], (int) args[1], orderBy);
        } else {
            PageHelper.startPage((int) args[0], (int) args[1]);
        }
        return sqlSession.selectList(bms.getListByConditionMSId(), conditionArgs);
    }

    private Object updateById(Object[] args) {
        Object updateObj = args[0];
        if (BeanUtil.getFieldValue(updateObj, "id") == null) {
            throw new IllegalArgumentException("Id can not be null");
        }
        return sqlSession.update(bms.getUpdateByIdMSId(), updateObj) > 0;
    }

    private Object updateByIdWithNull(Object[] args) {
        Object updateObj = args[0];
        if (BeanUtil.getFieldValue(updateObj, "id") == null) {
            throw new IllegalArgumentException("Id can not be null");
        }
        return sqlSession.update(bms.getUpdateByIdWithNullMSId(), updateObj) > 0;
    }

}