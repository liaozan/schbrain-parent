package com.schbrain.framework.autoconfigure.mybatis.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-27
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties implements ConfigurableProperties {

    @Override
    public String getNamespace() {
        return "jdbc-common";
    }

}