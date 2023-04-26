package com.schbrain.framework.autoconfigure.mybatis.datasource.customizer;

import com.schbrain.framework.autoconfigure.mybatis.properties.DataSourceConnectionProperties;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author liaozan
 * @since 2021/11/28
 */
public interface DataSourceCustomizer {

    void customize(DataSource dataSource, DataSourceConnectionProperties properties) throws SQLException;

}