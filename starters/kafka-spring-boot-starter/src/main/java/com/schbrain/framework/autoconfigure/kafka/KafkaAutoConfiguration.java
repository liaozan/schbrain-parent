package com.schbrain.framework.autoconfigure.kafka;

import com.schbrain.framework.autoconfigure.kafka.properties.KafkaProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerConfigUtils;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author liaozan
 * @since 2023-04-29
 */
@AutoConfiguration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {

    @Bean(KafkaListenerConfigUtils.KAFKA_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)
    public CustomKafkaListenerEndpointRegistry customKafkaListenerEndpointRegistry(KafkaProperties kafkaProperties) {
        return new CustomKafkaListenerEndpointRegistry(kafkaProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaMessageProducer defaultKafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaMessageProducer(kafkaTemplate);
    }

}
