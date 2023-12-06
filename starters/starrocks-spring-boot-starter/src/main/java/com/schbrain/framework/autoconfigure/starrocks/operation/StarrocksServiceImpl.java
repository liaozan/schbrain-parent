package com.schbrain.framework.autoconfigure.starrocks.operation;

import com.schbrain.common.util.ValidateUtils;
import com.schbrain.framework.autoconfigure.starrocks.annotation.StarrocksTable;
import com.schbrain.framework.autoconfigure.starrocks.properties.StarrocksProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.schbrain.framework.autoconfigure.starrocks.constants.StarrocksConstants.JDBC_URL_TEMPLATE;
import static com.schbrain.framework.autoconfigure.starrocks.constants.StarrocksConstants.STREAM_LOAD_TEMPLATE;

/**
 * @author liaozan
 * @since 2023/11/27
 */
public class StarrocksServiceImpl<T> implements StarrocksService<T>, InitializingBean {

    protected final Class<T> entityClass;
    protected final BeanPropertyRowMapper<T> rowMapper;

    @Autowired
    protected StarrocksProperties config;

    protected JdbcTemplate jdbcTemplate;
    protected StarrocksStreamLoadHandler handler;

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    public StarrocksServiceImpl() {
        this.entityClass = (Class<T>) ResolvableType.forInstance(this).getSuperType().getGeneric(0).getRawClass();
        this.rowMapper = new BeanPropertyRowMapper<>(entityClass);
    }

    @Override
    public void upsert(T entity) {
        upsert(entity, Collections.emptyList());
    }

    @Override
    public void upsert(T entity, List<String> columns) {
        upsertBatch(List.of(ValidateUtils.notNull(entity, "entity不能为空")), columns);
    }

    @Override
    public void upsertBatch(List<T> entityList) {
        upsertBatch(entityList, Collections.emptyList());
    }

    @Override
    public void upsertBatch(List<T> entityList, List<String> columns) {
        handler.upsertBatch(ValidateUtils.notEmpty(entityList, "entityList不能为空"), columns);
    }

    @Override
    public void delete(T entity) {
        deleteBatch(List.of(ValidateUtils.notNull(entity, "entity不能为空")));
    }

    @Override
    public void deleteBatch(List<T> entityList) {
        handler.deleteBatch(ValidateUtils.notNull(entityList, "entityList不能为空"));
    }

    @Override
    public List<T> search(PreparedStatementCreator callback) {
        return jdbcTemplate.queryForStream(callback, rowMapper).collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        StarrocksTable annotation = ValidateUtils.notNull(entityClass.getAnnotation(StarrocksTable.class), StarrocksTable.class.getName() + "不能为空");
        this.handler = createHandler(annotation.value());
        this.jdbcTemplate = createJdbcTemplate(annotation.value());
    }

    protected StarrocksStreamLoadHandler createHandler(String tableName) {
        String streamLoadUrl = String.format(STREAM_LOAD_TEMPLATE, config.getHost(), config.getHttpPort(), config.getDatabase(), tableName);
        return new StarrocksStreamLoadHandler(streamLoadUrl, config.getUsername(), config.getPassword());
    }

    protected JdbcTemplate createJdbcTemplate(String tableName) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName(String.format("%s:%s", config.getDatabase(), tableName));
        hikariConfig.setDriverClassName(config.getDriverClassName());
        hikariConfig.setJdbcUrl(String.format(JDBC_URL_TEMPLATE, config.getHost(), config.getPort(), config.getDatabase()));
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        return new JdbcTemplate(new HikariDataSource(hikariConfig));
    }

}
