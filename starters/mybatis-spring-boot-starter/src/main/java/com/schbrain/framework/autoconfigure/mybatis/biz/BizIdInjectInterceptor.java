package com.schbrain.framework.autoconfigure.mybatis.biz;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.schbrain.common.exception.BaseException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * @author liaozan
 * @since 2023-04-17
 */
public class BizIdInjectInterceptor implements InnerInterceptor {

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object entity) {
        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (sqlCommandType != SqlCommandType.INSERT) {
            return;
        }
        BizIdColumnField bizColumnField = BizIdHelper.getBizColumnField(entity.getClass());
        if (bizColumnField == null) {
            return;
        }
        doBizIdFill(entity, bizColumnField);
    }

    protected void doBizIdFill(Object entity, BizIdColumnField bizColumnField) {
        BizIdType bizIdType = bizColumnField.getAnnotation().type();
        if (bizIdType == BizIdType.INPUT) {
            return;
        }
        if (bizIdType == BizIdType.ID_WORKER) {
            Object bizIdValue = bizColumnField.getValue(entity);
            if (bizIdValue == null) {
                Object generatedBizId = bizIdType.generateBizId(entity);
                Class<?> bizIdFieldType = bizColumnField.getFieldType();
                Object convertedBizId = Convert.convertQuietly(bizIdFieldType, generatedBizId, null);
                if (convertedBizId == null) {
                    throw new BaseException(String.format("Cannot convert generated bizId value %s; From %s to %s", generatedBizId, generatedBizId.getClass(), bizIdFieldType));
                }
                bizColumnField.setValue(entity, convertedBizId);
            }
        }
    }

}