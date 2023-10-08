package com.schbrain.framework.autoconfigure.mybatis.constraint;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.*;

/**
 * @author liaozan
 * @since 2022/8/30
 */
public class DefaultTableMetaDataLoader implements TableMetaDataLoader {

    private static final String METADATA_QUERY = "SELECT c.`TABLE_NAME`,c.`COLUMN_NAME`,c.`COLUMN_DEFAULT`,c.`DATA_TYPE`,c.`EXTRA`,c.`IS_NULLABLE`,s.INDEX_NAME FROM INFORMATION_SCHEMA.COLUMNS c left join INFORMATION_SCHEMA.STATISTICS s on c.TABLE_SCHEMA = s.TABLE_SCHEMA and c.TABLE_NAME = s.TABLE_NAME and c.COLUMN_NAME = s.COLUMN_NAME WHERE c.TABLE_SCHEMA = ?";

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
