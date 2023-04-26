package com.schbrain.framework.support.spring.elasticsearch;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HttpContext;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author liaozan
 * @since 2022/5/6
 */
@AutoConfiguration
@ConditionalOnClass(ElasticsearchRestTemplate.class)
public class ElasticsearchFeatureAutoConfiguration {

    @Bean
    public RestClientBuilderCustomizer elasticsearchRestClientBuilderCustomizer() {
        return new RestClientBuilderCustomizer() {
            @Override
            public void customize(RestClientBuilder builder) {
            }

            @Override
            public void customize(HttpAsyncClientBuilder builder) {
                IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setSoKeepAlive(true).build();
                builder.setDefaultIOReactorConfig(ioReactorConfig);
                builder.setKeepAliveStrategy(new KeepAliveStrategy());
            }

            @Override
            public void customize(Builder builder) {
                builder.setSocketTimeout((int) Duration.ofMinutes(1).toMillis());
            }
        };
    }

    private static class KeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {

        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            long keepAliveDuration = super.getKeepAliveDuration(response, context);
            if (keepAliveDuration < 0) {
                keepAliveDuration = TimeUnit.MINUTES.toMillis(10);
            }
            return keepAliveDuration;
        }

    }

}