package com.schbrain.framework.autoconfigure.mybatis.datasource.customizer;

import com.schbrain.framework.autoconfigure.mybatis.datasource.extractor.DataSourcePropertiesExtractor;
import com.schbrain.framework.autoconfigure.mybatis.properties.DataSourceConnectionProperties;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * @author liaozan
 * @since 2021/11/28
 */
public class DefaultDataSourceCustomizer implements DataSourceCustomizer {

    private final List<DataSourcePropertiesExtractor> extractors;

    public DefaultDataSourceCustomizer(List<DataSourcePropertiesExtractor> extractors) {
        AnnotationAwareOrderComparator.sort(extractors);
        this.extractors = extractors;
    }

    @Override
    public void customize(DataSource dataSource, DataSourceConnectionProperties properties) throws SQLException {
        Map<String, String> connectionProps = properties.toConfigurationMap();
        for (DataSourcePropertiesExtractor extractor : extractors) {
            if (extractor.support(dataSource)) {
                Properties originProps = extractor.extract(dataSource, connectionProps);
                if (originProps != null) {
                    originProps.putAll(connectionProps);
                }
            }
        }
    }

}
