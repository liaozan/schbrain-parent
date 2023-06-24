package com.schbrain.framework.autoconfigure.mybatis.datasource.extractor;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author liaozan
 * @since 2021/11/23
 */
@ConditionalOnClass(HikariDataSource.class)
public class HikariDataSourcePropertiesExtractor extends DataSourcePropertiesExtractorSupport {

    @Override
    protected Class<? extends DataSource> getSupportedType() {
        return HikariDataSource.class;
    }

    @Override
    protected Properties extract(DataSource dataSource) throws SQLException {
        return dataSource.unwrap(HikariDataSource.class).getDataSourceProperties();
    }

}