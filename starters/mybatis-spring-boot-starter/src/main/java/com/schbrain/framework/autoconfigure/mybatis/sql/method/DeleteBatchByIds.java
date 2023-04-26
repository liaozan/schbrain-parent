package com.schbrain.framework.autoconfigure.mybatis.sql.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.schbrain.framework.autoconfigure.mybatis.util.SqlUtils;

/**
 * @author liaozan
 * @since 2021/11/26
 */
public class DeleteBatchByIds extends com.baomidou.mybatisplus.core.injector.methods.DeleteBatchByIds {

    private static final long serialVersionUID = -6821464569587694540L;

    @Override
    protected String sqlLogicSet(TableInfo table) {
        String logicSet = super.sqlLogicSet(table);
        return SqlUtils.withLogicDeleteVersionIfNecessary(table, logicSet);
    }

}