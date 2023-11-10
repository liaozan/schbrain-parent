package com.schbrain.framework.autoconfigure.mybatis.datasource.extractor;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * @author liaozan
 * @since 2021/11/23
 */
public interface DataSourcePropertiesExtractor {

    boolean support(DataSource dataSource);

    Properties extract(DataSource dataSource, Map<String, String> properties) throws SQLException;

}
