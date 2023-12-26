package com.schbrain.framework.autoconfigure.starrocks.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.schbrain.common.entity.CanalChangedEvent;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.List;

/**
 * @author liaozan
 * @since 2023/12/22
 */
public class ConvertUtils {

    private static final ObjectMapper DESERIALIZER = JacksonUtils.getObjectMapper().copy().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public static <Target> Target convertTo(ConsumerRecord<String, String> record, Class<Target> targetType) {
        CanalChangedEvent event = JacksonUtils.getObjectFromJson(record.value(), CanalChangedEvent.class);
        return convertTo(event, targetType);
    }

    public static <Target> Target convertTo(CanalChangedEvent event, Class<Target> targetType) {
        if (event == null) {
            throw new BaseException("CanalChangedEvent is null");
        }
        return DESERIALIZER.convertValue(event.getAfter(), targetType);
    }

    public static <Target> List<Target> convertToList(ConsumerRecords<String, String> records, Class<Target> targetType) {
        return StreamUtils.toList(records, record -> convertTo(record, targetType));
    }

    public static <Source, Target> Target convertTo(ConsumerRecord<String, String> record, Class<Source> sourceType, Class<Target> targetType) {
        Source source = convertTo(record, sourceType);
        return BeanCopyUtils.copy(source, targetType);
    }

    public static <Source, Target> List<Target> convertToList(ConsumerRecords<String, String> records, Class<Source> sourceType, Class<Target> targetType) {
        return StreamUtils.toList(records, record -> convertTo(record, sourceType, targetType));
    }

}
