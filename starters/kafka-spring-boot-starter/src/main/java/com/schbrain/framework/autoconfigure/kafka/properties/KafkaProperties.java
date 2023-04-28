package com.schbrain.framework.autoconfigure.kafka.properties;

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
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties extends ConfigurableProperties {

    @Override
    public String getDefaultNamespace() {
        return "kafka-common";
    }

}