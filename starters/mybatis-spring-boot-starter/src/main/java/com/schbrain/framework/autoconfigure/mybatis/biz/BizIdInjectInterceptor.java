package com.schbrain.framework.autoconfigure.mybatis.biz;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.schbrain.framework.autoconfigure.mybatis.core.BizIdColumnField;
import org.apache.commons.lang3.StringUtils;
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
            String bizIdValue = bizColumnField.getValue(entity);
            if (StringUtils.isBlank(bizIdValue)) {
                bizColumnField.setValue(entity, bizIdType.generateBizId(entity));
            }
        }
    }

}