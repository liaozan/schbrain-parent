package com.schbrain.framework.autoconfigure.mybatis.datasource.extractor;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author liaozan
 * @since 2021/11/23
 */
@Slf4j
@ConditionalOnClass(DruidDataSource.class)
public class DruidDataSourcePropertiesExtractor extends DataSourcePropertiesExtractorSupport {

    @Override
    protected Class<DruidDataSource> getSupportedType() {
        return DruidDataSource.class;
    }

    @Override
    protected Properties extract(DataSource dataSource) throws SQLException {
        return dataSource.unwrap(DruidDataSource.class).getConnectProperties();
    }

}