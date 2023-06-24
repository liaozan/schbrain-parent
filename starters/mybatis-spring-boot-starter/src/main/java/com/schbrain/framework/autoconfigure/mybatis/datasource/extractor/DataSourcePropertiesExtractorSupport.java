package com.schbrain.framework.autoconfigure.mybatis.datasource.extractor;

import org.springframework.util.TypeUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * @author liaozan
 * @since 2021/11/23
 */
public abstract class DataSourcePropertiesExtractorSupport implements DataSourcePropertiesExtractor {

    @Override
    public boolean support(DataSource dataSource) {
        Class<? extends DataSource> supportedType = getSupportedType();
        return TypeUtils.isAssignable(supportedType, dataSource.getClass());
    }

    @Override
    public Properties extract(DataSource dataSource, Map<String, String> properties) throws SQLException {
        return extract(dataSource);
    }

    protected abstract Class<? extends DataSource> getSupportedType();

    protected abstract Properties extract(DataSource dataSource) throws SQLException;

}