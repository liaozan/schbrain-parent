package com.schbrain.framework.autoconfigure.elasticsearch.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-29
 */
@Data
@ConfigurationProperties(prefix = "spring.elasticsearch")
public class ElasticsearchProperties implements ConfigurableProperties {

    @Override
    public String getNamespace() {
        return "elasticsearch-common";
    }

}