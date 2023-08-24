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
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    public Map<Long, T> getMapByIds(Collection<Long> ids) {
        // Cannot call the override method here, because override method use mapper to judge the fields to select
        if (isEmpty(ids)) {
            return emptyMap();
        }
        return StreamUtils.toMap(listByIds(ids), T::getId);
    }

    @Override
    public <V> Map<Long, V> getMapByIds(Collection<Long> ids, SFunction<T, V> mapper) {
        if (isEmpty(ids)) {
            return emptyMap();
        }
        // noinspection unchecked
        List<T> dataList = lambdaQuery().select(T::getId, mapper).in(T::getId, ids).list();
        return StreamUtils.toMap(dataList, T::getId, mapper);
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
    public <K> List<T> listByBizIds(Collection<K> bizIds) {
        if (isEmpty(bizIds)) {
            return emptyList();
        }
        return query().in(getBidColumnField().getColumnName(), bizIds).list();
    }

    @Override
    public <K> Map<K, T> getMapByBizIds(Collection<K> bizIds) {
        // Cannot call the override method here, because override method use mapper to judge the fields to select
        if (isEmpty(bizIds)) {
            return emptyMap();
        }
        return StreamUtils.toMap(listByBizIds(bizIds), entity -> getBidColumnField().getValue(entity));
    }

    @Override
    public <K, V> Map<K, V> getMapByBizIds(Collection<K> bizIds, SFunction<T, V> mapper) {
        if (isEmpty(bizIds)) {
            return emptyMap();
        }
        // How to get the mapper column ?
        return StreamUtils.toMap(listByBizIds(bizIds), entity -> getBidColumnField().getValue(entity), mapper);
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
