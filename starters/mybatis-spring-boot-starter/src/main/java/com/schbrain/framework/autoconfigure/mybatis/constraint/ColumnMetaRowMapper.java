package com.schbrain.framework.autoconfigure.mybatis.constraint;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author liaozan
 * @since 2022/8/30
 */
public class ColumnMetaRowMapper implements RowMapper<ColumnMeta> {

    @Override
    public ColumnMeta mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ColumnMeta columnMeta = new ColumnMeta();
        columnMeta.setTableName(resultSet.getString("TABLE_NAME"));
        columnMeta.setColumnName(resultSet.getString("COLUMN_NAME"));
        columnMeta.setDataType(resultSet.getString("DATA_TYPE"));
        columnMeta.setNullable(isNullable(resultSet.getString("IS_NULLABLE")));
        columnMeta.setColumnDefault(resultSet.getString("COLUMN_DEFAULT"));
        columnMeta.setExtra(resultSet.getString("EXTRA"));
        return columnMeta;
    }

    private boolean isNullable(String value) {
        return "YES".equalsIgnoreCase(value);
    }

}