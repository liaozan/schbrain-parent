package com.schbrain.framework.autoconfigure.kafka.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.kafka.CustomKafkaListenerEndpointRegistry;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;

/**
 * @author liaozan
 * @since 2023-04-29
 */
@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties implements ConfigurableProperties {

    /**
     * 消费者配置
     */
    @NestedConfigurationProperty
    private Consumer consumer = new Consumer();

    @Override
    public String getNamespace() {
        return "kafka-common";
    }

    @Data
    public static class Consumer {

        /**
         * 是否启用消费者,只对在本地运行生效
         *
         * @see CustomKafkaListenerEndpointRegistry#registerListenerContainer(KafkaListenerEndpoint, KafkaListenerContainerFactory, boolean)
         */
        private boolean enabled = false;

    }

}
