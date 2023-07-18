package com.schbrain.framework.autoconfigure.kafka;

import com.schbrain.common.util.EnvUtils;
import com.schbrain.framework.autoconfigure.kafka.properties.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

/**
 * @author liaozan
 * @since 2023/7/18
 */
@Slf4j
public class CustomKafkaListenerEndpointRegistry extends KafkaListenerEndpointRegistry {

    private static final String CONSUMER_ENABLED_KEY = "spring.kafka.consumer.enabled";

    private final KafkaProperties kafkaProperties;

    public CustomKafkaListenerEndpointRegistry(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Override
    public void registerListenerContainer(KafkaListenerEndpoint endpoint, KafkaListenerContainerFactory<?> factory, boolean startImmediately) {
        if (EnvUtils.runningOnCloudPlatform() || kafkaProperties.getConsumer().isEnabled()) {
            super.registerListenerContainer(endpoint, factory, startImmediately);
        } else {
            log.warn("Not running on CloudPlatform or {} is set to false, will not listen to messages from brokers", CONSUMER_ENABLED_KEY);
            log.warn("If you want force to register with Kafka Brokers, set {} = true", CONSUMER_ENABLED_KEY);
        }
    }

}
