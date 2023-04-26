package com.schbrain.framework.autoconfigure.mybatis.datasource;

import com.schbrain.framework.autoconfigure.mybatis.datasource.customizer.DataSourceCustomizer;
import com.schbrain.framework.autoconfigure.mybatis.properties.DataSourceConnectionProperties;
import com.schbrain.framework.support.spring.BeanPostProcessorAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author liaozan
 * @since 2021/11/23
 */
@Slf4j
public class DataSourceConnectionPostProcessor extends BeanPostProcessorAdapter<DataSource> {

    // use ObjectProvider to avoid early initialization beans
    private final ObjectProvider<DataSourceCustomizer> customizers;
    private final ObjectProvider<DataSourceConnectionProperties> connectionProperties;

    public DataSourceConnectionPostProcessor(ObjectProvider<DataSourceCustomizer> customizers,
                                             ObjectProvider<DataSourceConnectionProperties> connectionProperties) {
        this.customizers = customizers;
        this.connectionProperties = connectionProperties;
    }

    @Override
    protected void processBeforeInitialization(DataSource dataSource, String beanName) throws BeansException {
        DataSourceConnectionProperties connectionProperties = this.connectionProperties.getObject();
        customizers.orderedStream().forEach(customizer -> {
            try {
                customizer.customize(dataSource, connectionProperties);
            } catch (SQLException e) {
                log.warn("failed to customize dataSource connectionProperties", e);
            }
        });
    }

}