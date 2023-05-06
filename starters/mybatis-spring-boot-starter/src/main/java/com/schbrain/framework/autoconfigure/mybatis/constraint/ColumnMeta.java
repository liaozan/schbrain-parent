package com.schbrain.framework.autoconfigure.mybatis.constraint;

import lombok.Data;

/**
 * @author liaozan
 * @since 2022/8/30
 */
@Data
public class ColumnMeta {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 是否允许为空
     */
    private boolean nullable;

    /**
     * 列默认值
     */
    private String columnDefault;

    /**
     * 扩展信息
     */
    private String extra;

    /**
     * 索引名称
     */
    private String indexName;

}