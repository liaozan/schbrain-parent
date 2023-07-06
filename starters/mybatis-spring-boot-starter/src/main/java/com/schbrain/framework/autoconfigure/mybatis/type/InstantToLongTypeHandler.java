package com.schbrain.framework.autoconfigure.mybatis.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

/**
 * @author liaozan
 * @since 2022/8/31
 */
public class InstantToLongTypeHandler extends BaseTypeHandler<Instant> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int index, Instant parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(index, parameter.toEpochMilli());
    }

    @Override
    public Instant getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long result = rs.getLong(columnName);
        return result == 0 && rs.wasNull() ? null : Instant.ofEpochMilli(result);
    }

    @Override
    public Instant getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long result = rs.getLong(columnIndex);
        return result == 0 && rs.wasNull() ? null : Instant.ofEpochMilli(result);
    }

    @Override
    public Instant getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long result = cs.getLong(columnIndex);
        return result == 0 && cs.wasNull() ? null : Instant.ofEpochMilli(result);
    }

}