package com.schbrain.framework.autoconfigure.elasticsearch.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.elasticsearch")
public class ElasticsearchProperties extends ConfigurableProperties {

    @Override
    public String getDefaultNamespace() {
        return "elasticsearch-common";
    }

}