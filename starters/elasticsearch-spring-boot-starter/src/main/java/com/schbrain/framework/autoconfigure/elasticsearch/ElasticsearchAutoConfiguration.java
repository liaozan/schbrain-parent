package com.schbrain.framework.autoconfigure.elasticsearch;

import com.schbrain.framework.autoconfigure.elasticsearch.properties.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author liaozan
 * @since 2023-04-29
 */
@AutoConfiguration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchAutoConfiguration {

}