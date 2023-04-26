package com.schbrain.framework.autoconfigure.mybatis.constraint;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.*;

/**
 * @author liaozan
 * @since 2022/8/30
 */
public class DefaultTableMetaDataLoader implements TableMetaDataLoader {

    public static final String METADATA_QUERY = "SELECT `TABLE_NAME`,`COLUMN_NAME`,`COLUMN_DEFAULT`,`DATA_TYPE`,`EXTRA`,`IS_NULLABLE` FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?";

    private final RowMapper<ColumnMeta> rowMapper = new ColumnMetaRowMapper();

    private final JdbcTemplate jdbcTemplate;

    public DefaultTableMetaDataLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, List<ColumnMeta>> loadTableMeta(String database) {
        List<ColumnMeta> columnMetas = jdbcTemplate.query(METADATA_QUERY, rowMapper, database);
        Map<String, List<ColumnMeta>> metaMap = new HashMap<>();
        for (ColumnMeta columnMeta : columnMetas) {
            String tableName = columnMeta.getTableName();
            List<ColumnMeta> metaList = metaMap.computeIfAbsent(tableName, name -> new ArrayList<>());
            metaList.add(columnMeta);
        }
        return metaMap;
    }

}