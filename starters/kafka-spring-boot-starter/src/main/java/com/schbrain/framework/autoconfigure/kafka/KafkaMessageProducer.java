package com.schbrain.framework.autoconfigure.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @author liaozan
 * @since 2023/7/17
 */
@Slf4j
public class KafkaMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * producer 异步方式发送数据
     *
     * @param topic topic名称
     * @param message producer发送的数据
     * @param description 消息描述
     */
    public void sendMessageAsync(String topic, String message, String description) {
        sendMessageAsync(topic, message, new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                log.debug("{} 消息发送成功, message: {}", description, message);
            }

            @Override
            public void onFailure(Throwable exception) {
                log.error("{} 消息发送失败, {}", description, exception.getMessage(), exception);
            }
        });
    }

    /**
     * producer 异步方式发送数据
     *
     * @param topic topic名称
     * @param message producer发送的数据
     */
    public void sendMessageAsync(String topic, String message, ListenableFutureCallback<SendResult<String, String>> callback) {
        try {
            kafkaTemplate.send(topic, message).addCallback(callback);
        } catch (Exception exception) {
            log.error("消息发送失败, {}", exception.getMessage(), exception);
        }
    }

}
