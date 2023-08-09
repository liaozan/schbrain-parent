package com.schbrain.common.web;

import com.schbrain.common.web.support.LongToStringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author liaozan
 * @since 2023/8/9
 */
@ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
public class ObjectMapperConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.serializerByType(Long.class, LongToStringSerializer.instance);
    }

}
