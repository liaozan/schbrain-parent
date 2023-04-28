package com.schbrain.framework.autoconfigure.kafka;

import com.schbrain.framework.autoconfigure.kafka.properties.KafkaProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-29
 */
@AutoConfiguration
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {

}