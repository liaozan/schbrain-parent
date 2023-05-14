package com.schbrain.framework.autoconfigure.kafka.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-29
 */
@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties implements ConfigurableProperties {

    @Override
    public String getNamespace() {
        return "kafka-common";
    }

}