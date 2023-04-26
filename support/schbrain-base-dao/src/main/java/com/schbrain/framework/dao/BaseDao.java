package com.schbrain.framework.dao;

import com.github.pagehelper.Page;

import java.util.List;

/**
 * description
 *
 * @author liwu on 2019/7/29
 */
public interface BaseDao<T> {

    String getTableName();

    /**
     * 插入单个领域对象
     * <p><strong>注意：</strong>插入时只会指定非<tt>null</tt>的领域对象属性对应的列</p>
     *
     * @param obj 待插入领域对象
     * @return 插入是否成功
     */
    Boolean add(T obj);

    /**
     * 批量插入领域对象
     *
     * @param objList 待插入领域对象列表
     * @param fields  插入时指定的领域对象属性列表，如果为空表示对象的所有属性
     * @return 影响行数
     */
    Integer addList(List<T> objList, String... fields);

    T getById(long id);

    T getOneByObject(T obj);

    T getOneByCondition(String whereClause, Object... objs);

    List<T> listByIdList(List<Long> idList);

    List<T> listByCondition(String whereClause, Object... objs);

    List<T> listByObject(T obj);

    Integer getCountByCondition(String whereClause, Object... objs);

    Page<T> pageByCondition(int pageNum, int pageSize, String whereClause, Object... objs);

    /**
     * 分页获取列表
     *
     * @param pageNum       当前页码
     * @param pageSize      当前页记录数
     * @param whereClause   <tt>where</tt>关键词后的条件语句
     * @param orderByClause <tt>order by</tt>关键词后的排序语句，注意：语句中不支持参数
     * @param objs          <tt>where</tt>关键词后的条件语句中参数对应的值
     * @return <tt>page</tt>对象，包含记录及分页信息
     */
    Page<T> pageByCondition(int pageNum, int pageSize, String whereClause, String orderByClause, Object... objs);

    Boolean deleteById(long id);

    Integer deleteByIdList(List<Long> idList);

    Integer deleteByCondition(String whereClause, Object... objs);

    Boolean updateById(T obj);

    Boolean updateByIdWithNull(T obj);

    Integer updateByCondition(T obj, String whereClause, Object... objs);

    Integer updateByCompleteSql(String completeSql, Object... objs);

}