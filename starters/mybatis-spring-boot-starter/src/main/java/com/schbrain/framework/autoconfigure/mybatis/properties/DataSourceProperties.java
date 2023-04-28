package com.schbrain.framework.autoconfigure.mybatis.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties extends ConfigurableProperties {

    @Override
    public String getDefaultNamespace() {
        return "jdbc-common";
    }

}