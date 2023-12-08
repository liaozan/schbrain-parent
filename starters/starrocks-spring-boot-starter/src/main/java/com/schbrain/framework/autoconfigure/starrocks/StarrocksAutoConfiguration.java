package com.schbrain.framework.autoconfigure.starrocks;

import com.schbrain.framework.autoconfigure.starrocks.properties.StarrocksProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static com.schbrain.framework.autoconfigure.starrocks.constants.StarrocksConstants.JDBC_URL_TEMPLATE;

/**
 * @author liaozan
 * @since 2023/11/27
 */
@AutoConfiguration
@EnableConfigurationProperties(StarrocksProperties.class)
public class StarrocksAutoConfiguration {

    @Bean("starrocksJdbcTemplate")
    public NamedParameterJdbcTemplate starrocksJdbcTemplate(StarrocksProperties config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(config.getDatabase());
        hikariConfig.setDriverClassName(config.getDriverClassName());
        hikariConfig.setJdbcUrl(String.format(JDBC_URL_TEMPLATE, config.getHost(), config.getPort(), config.getDatabase()));
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        return new NamedParameterJdbcTemplate(new HikariDataSource(hikariConfig));
    }

}
