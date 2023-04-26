package com.schbrain.framework.autoconfigure.mybatis.constraint;

import java.util.List;
import java.util.Map;

/**
 * @author liaozan
 * @since 2022/8/30
 */
public interface TableMetaDataLoader {

    /**
     * 加载指定数据库的所有表元信息
     */
    Map<String, List<ColumnMeta>> loadTableMeta(String database);

}