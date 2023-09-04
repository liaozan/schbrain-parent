package com.schbrain.framework.autoconfigure.mybatis.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * @author liaozan
 * @since 2023/8/29
 */
public class LambdaUtils {

    public static <T> String getColumnName(SFunction<T, ?> column) {
        return new LambdaQueryWrapperExt<T>().columnToString(column);
    }

    /**
     * Extend LambdaQueryWrapper to expose columnToString method
     */
    private static class LambdaQueryWrapperExt<T> extends LambdaQueryWrapper<T> {

        private static final long serialVersionUID = 943345355018736972L;

        @Override
        protected String columnToString(SFunction<T, ?> column) {
            return super.columnToString(column);
        }

    }

}
