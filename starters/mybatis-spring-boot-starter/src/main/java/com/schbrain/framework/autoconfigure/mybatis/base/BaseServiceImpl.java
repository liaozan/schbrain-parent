package com.schbrain.framework.autoconfigure.mybatis.base;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.StreamUtils;
import com.schbrain.common.util.support.ValidateSupport;
import com.schbrain.framework.autoconfigure.mybatis.annotation.BizId;
import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdColumnField;
import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdHelper;
import com.schbrain.framework.autoconfigure.mybatis.exception.NoSuchRecordException;
import com.schbrain.framework.autoconfigure.mybatis.util.LambdaUtils;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * @author liaozan
 * @since 2021/10/14
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T>, ValidateSupport, InitializingBean {

    @Nullable
    private BizIdColumnField bizIdColumnField;

    @Override
    public T getById(Serializable id) {
        return getById((Long) id, false);
    }

    @Override
    public T getById(Long id, boolean throwIfNotFound) {
        Supplier<? extends RuntimeException> notFoundSupplier = null;
        if (throwIfNotFound) {
            notFoundSupplier = () -> new NoSuchRecordException(entityClass);
        }
        return getById(id, notFoundSupplier);
    }

    @Override
    public T getById(Long id, Supplier<? extends RuntimeException> notFoundSupplier) {
        T entity = super.getById(id);
        if (entity == null && notFoundSupplier != null) {
            throw notFoundSupplier.get();
        }
        return entity;
    }

    @Override
    public <V> V getById(Long id, SFunction<T, V> column) {
        return getById(id, column, false);
    }

    @Override
    public <V> V getById(Long id, SFunction<T, V> column, boolean throwIfNotFound) {
        Supplier<? extends RuntimeException> notFoundSupplier = null;
        if (throwIfNotFound) {
            notFoundSupplier = () -> new NoSuchRecordException(entityClass);
        }
        return getById(id, column, notFoundSupplier);
    }

    @Override
    public <V> V getById(Long id, SFunction<T, V> column, Supplier<? extends RuntimeException> notFoundSupplier) {
        T entity = lambdaQuery().select(column).eq(T::getId, id).one();
        if (entity == null && notFoundSupplier != null) {
            throw notFoundSupplier.get();
        }
        return entity == null ? null : column.apply(entity);
    }

    @Override
    public Map<Long, T> getMapByIds(Collection<Long> ids) {
        // Cannot call the override method here, because override method use column to judge the fields to select
        if (isEmpty(ids)) {
            return emptyMap();
        }
        return StreamUtils.toMap(listByIds(ids), T::getId);
    }

    @Override
    public <V> Map<Long, V> getMapByIds(Collection<Long> ids, SFunction<T, V> column) {
        if (isEmpty(ids)) {
            return emptyMap();
        }
        List<T> dataList = lambdaQuery().select(T::getId, column).in(T::getId, ids).list();
        return StreamUtils.toMap(dataList, T::getId, column);
    }

    @Override
    public T getByBizId(Object bizId) {
        return getByBizId(bizId, false);
    }

    @Override
    public T getByBizId(Object bizId, boolean throwIfNotFound) {
        Supplier<? extends RuntimeException> notFoundSupplier = null;
        if (throwIfNotFound) {
            notFoundSupplier = () -> new NoSuchRecordException(entityClass);
        }
        return getByBizId(bizId, notFoundSupplier);
    }

    @Override
    public T getByBizId(Object bizId, Supplier<? extends RuntimeException> notFoundSupplier) {
        T entity = query().eq(getBidColumnField().getColumnName(), bizId).one();
        if (entity == null && notFoundSupplier != null) {
            throw notFoundSupplier.get();
        }
        return entity;
    }

    @Override
    public <V> V getByBizId(Object bizId, SFunction<T, V> column) {
        return getByBizId(bizId, column, false);
    }

    @Override
    public <V> V getByBizId(Object bizId, SFunction<T, V> column, boolean throwIfNotFound) {
        Supplier<? extends RuntimeException> notFoundSupplier = null;
        if (throwIfNotFound) {
            notFoundSupplier = () -> new NoSuchRecordException(entityClass);
        }
        return getByBizId(bizId, column, notFoundSupplier);
    }

    @Override
    public <V> V getByBizId(Object bizId, SFunction<T, V> column, Supplier<? extends RuntimeException> notFoundSupplier) {
        T entity = query()
                .select(LambdaUtils.getColumnName(column))
                .eq(getBidColumnField().getColumnName(), bizId)
                .one();
        if (entity == null && notFoundSupplier != null) {
            throw notFoundSupplier.get();
        }
        return entity == null ? null : column.apply(entity);
    }

    @Override
    public <V> List<V> listByIds(Collection<Long> ids, SFunction<T, V> column) {
        if (isEmpty(ids)) {
            return emptyList();
        }
        List<T> dataList = lambdaQuery().select(column).in(T::getId, ids).list();
        return StreamUtils.toList(dataList, column);
    }

    @Override
    public <K> List<T> listByBizIds(Collection<K> bizIds) {
        // Cannot call the override method here, because override method use column to judge the fields to select
        if (isEmpty(bizIds)) {
            return emptyList();
        }
        return query().in(getBidColumnField().getColumnName(), bizIds).list();
    }

    @Override
    public <K, V> List<V> listByBizIds(Collection<K> bizIds, SFunction<T, V> column) {
        if (isEmpty(bizIds)) {
            return emptyList();
        }
        List<T> dataList = query()
                .select(LambdaUtils.getColumnName(column))
                .in(getBidColumnField().getColumnName(), bizIds)
                .list();
        return StreamUtils.toList(dataList, column);
    }

    @Override
    public <K> Map<K, T> getMapByBizIds(Collection<K> bizIds) {
        // Cannot call the override method here, because override method use column to judge the fields to select
        if (isEmpty(bizIds)) {
            return emptyMap();
        }
        return StreamUtils.toMap(listByBizIds(bizIds), entity -> getBidColumnField().getValue(entity));
    }

    @Override
    public <K, V> Map<K, V> getMapByBizIds(Collection<K> bizIds, SFunction<T, V> column) {
        if (isEmpty(bizIds)) {
            return emptyMap();
        }
        String bizIdColumnName = getBidColumnField().getColumnName();
        List<T> dataList = query()
                .select(bizIdColumnName, LambdaUtils.getColumnName(column))
                .in(bizIdColumnName, bizIds)
                .list();
        return StreamUtils.toMap(dataList, entity -> getBidColumnField().getValue(entity), column);
    }

    @Override
    public boolean updateByIdWithNull(T entity) {
        return SqlHelper.retBool(getBaseMapper().alwaysUpdateSomeColumnById(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchByIdsWithNull(Collection<T> entityList) {
        return updateBatchByIdsWithNull(entityList, DEFAULT_BATCH_SIZE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatchByIdsWithNull(Collection<T> entityList, int batchSize) {
        String sqlStatement = getUpdateByIdWithNullStatementId(mapperClass);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            ParamMap<T> param = new ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    @Override
    public <K> boolean removeByBizId(K bizId) {
        return update().eq(getBidColumnField().getColumnName(), bizId).remove();
    }

    @Override
    public <K> boolean removeBatchByBizIds(Collection<K> bizIds) {
        if (isEmpty(bizIds)) {
            return false;
        }
        return update().in(getBidColumnField().getColumnName(), bizIds).remove();
    }

    @Override
    public void afterPropertiesSet() {
        ReflectionUtils.doWithFields(entityClass, bizId -> {
            if (this.bizIdColumnField != null) {
                throw new BaseException(String.format("@BizId can't more than one in Class: \"%s\"", entityClass.getName()));
            }
            this.bizIdColumnField = new BizIdColumnField(entityClass, bizId);
            BizIdHelper.putBizColumnField(entityClass, bizIdColumnField);
        }, field -> field.isAnnotationPresent(BizId.class));
    }

    protected BizIdColumnField getBidColumnField() {
        if (bizIdColumnField == null) {
            throw new BaseException(String.format("@BizId not exist in Class: \"%s\"", entityClass.getName()));
        }
        return bizIdColumnField;
    }

    private String getUpdateByIdWithNullStatementId(Class<M> mapperClass) {
        return mapperClass.getName() + StringPool.DOT + "alwaysUpdateSomeColumnById";
    }

}
