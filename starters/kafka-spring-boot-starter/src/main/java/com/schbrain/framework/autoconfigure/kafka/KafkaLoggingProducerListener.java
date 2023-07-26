package com.schbrain.framework.autoconfigure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.LoggingProducerListener;

/**
 * @author liaozan
 * @since 2023/7/26
 */
@Slf4j
public class KafkaLoggingProducerListener extends LoggingProducerListener<Object, Object> {

    public KafkaLoggingProducerListener() {
        this.setIncludeContents(false);
    }

    @Override
    public void onSuccess(ProducerRecord<Object, Object> record, RecordMetadata metadata) {
        log.debug("[{}] 消息发送成功, content: {}", record.topic(), record.value());
    }

    @Override
    public void onError(ProducerRecord<Object, Object> record, RecordMetadata metadata, Exception exception) {
        log.warn("[{}] 消息发送失败, content: {}", record.topic(), record.value());
        super.onError(record, metadata, exception);
    }

}
