package com.schbrain.framework.autoconfigure.mybatis.sql.method;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.schbrain.framework.autoconfigure.mybatis.util.SqlUtils;

/**
 * @author liaozan
 * @since 2021/11/26
 */
public class Delete extends com.baomidou.mybatisplus.core.injector.methods.Delete {

    private static final long serialVersionUID = -4047186946220703735L;

    @Override
    protected String sqlLogicSet(TableInfo table) {
        String logicSet = super.sqlLogicSet(table);
        return SqlUtils.withLogicDeleteVersionIfNecessary(table, logicSet);
    }

}