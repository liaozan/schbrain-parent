package com.schbrain.framework.autoconfigure.mybatis.base;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author liaozan
 * @since 2021/10/14
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T>, ValidateSupport, InitializingBean {

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
        if (isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return StreamUtils.toMap(super.listByIds(ids), T::getId);
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
        assertBidColumnFieldExist();
        T entity = query().eq(bizIdColumnField.getColumnName(), bizId).one();
        if (entity == null && notFoundSupplier != null) {
            throw notFoundSupplier.get();
        }
        return entity;
    }

    @Override
    public <K> List<T> listByBizIds(Collection<K> bizIds) {
        assertBidColumnFieldExist();
        if (isEmpty(bizIds)) {
            return Collections.emptyList();
        }
        return query().in(bizIdColumnField.getColumnName(), bizIds).list();
    }

    @Override
    public <K> Map<K, T> getMapByBizIds(Collection<K> bizIds) {
        assertBidColumnFieldExist();
        if (isEmpty(bizIds)) {
            return Collections.emptyMap();
        }
        return StreamUtils.toMap(listByBizIds(bizIds), entity -> bizIdColumnField.getValue(entity));
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

    private void assertBidColumnFieldExist() {
        if (bizIdColumnField == null) {
            throw new BaseException(String.format("@BizId not exist in Class: \"%s\"", entityClass.getName()));
        }
    }

    private String getUpdateByIdWithNullStatementId(Class<M> mapperClass) {
        return mapperClass.getName() + StringPool.DOT + "alwaysUpdateSomeColumnById";
    }

}