package com.schbrain.framework.autoconfigure.mybatis.listener;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.StreamUtils;
import com.schbrain.framework.autoconfigure.mybatis.base.*;
import com.schbrain.framework.autoconfigure.mybatis.constraint.*;
import com.schbrain.framework.autoconfigure.mybatis.constraint.Table.FieldInfo;
import com.schbrain.framework.autoconfigure.mybatis.exception.TableConstraintException;
import com.schbrain.framework.autoconfigure.mybatis.properties.MybatisProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author liaozan
 * @since 2022/8/28
 */
@Slf4j
public class TableConstraintCheckerBean implements SmartInitializingSingleton, BeanFactoryAware {

    private final DataSource dataSource;

    private final MybatisProperties mybatisProperties;

    private ConfigurableListableBeanFactory beanFactory;

    public TableConstraintCheckerBean(DataSource dataSource, MybatisProperties mybatisProperties) {
        this.dataSource = dataSource;
        this.mybatisProperties = mybatisProperties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (!mybatisProperties.isEnableTableConstraintCheck()) {
            log.warn("Table constraint check is disabled");
            return;
        }

        log.info("Table constraint check started");

        List<TableMetaDataLoader> metaDataLoaders = beanFactory.getBeanProvider(TableMetaDataLoader.class).orderedStream().collect(toList());
        if (CollectionUtils.isEmpty(metaDataLoaders)) {
            JdbcTemplate jdbcTemplate = beanFactory.getBean(JdbcTemplate.class);
            // Avoid add to a immutable collection
            metaDataLoaders = List.of(new DefaultTableMetaDataLoader(jdbcTemplate));
        }

        Map<String, List<ColumnMeta>> tableMetadata = loadTableMetadata(metaDataLoaders);
        if (MapUtils.isEmpty(tableMetadata)) {
            log.warn("Table metadata is empty, ignore table constraint check");
            return;
        }

        List<TableConstraintChecker> checkers = beanFactory.getBeanProvider(TableConstraintChecker.class).orderedStream().collect(toList());
        if (CollectionUtils.isEmpty(checkers)) {
            // Avoid add to a immutable collection
            checkers = List.of(new DefaultTableConstraintChecker());
        }

        List<TableConstraintException> errors = new ArrayList<>();
        for (Class<?> mapper : getAllMappers()) {
            doConstraintCheck(mapper, tableMetadata, checkers, errors);
        }

        if (CollectionUtils.isNotEmpty(errors)) {
            throw new TableConstraintException(errors);
        }

        log.info("Table constraint check completed");
        beanFactory.destroyBean(this);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory);
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    private void doConstraintCheck(Class<?> mapperClass, Map<String, List<ColumnMeta>> tableMetadata,
                                   List<TableConstraintChecker> checkers, List<TableConstraintException> errors) {
        if (!ClassUtils.isAssignable(BaseMapper.class, mapperClass)) {
            return;
        }

        Class<?> entityClass = ReflectionKit.getSuperClassGenericType(mapperClass, Mapper.class, 0);
        if (!ClassUtils.isAssignable(BaseEntity.class, entityClass)) {
            return;
        }

        if (entityClass.isAssignableFrom(IgnoreConstraintCheck.class)) {
            return;
        }

        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        if (tableInfo == null) {
            errors.add(new TableConstraintException("Could not get tableInfo for {}", entityClass.getName()));
            return;
        }

        Map<String, ColumnMeta> columnMetaMap = StreamUtils.toMap(tableMetadata.get(tableInfo.getTableName()), ColumnMeta::getColumnName);
        if (MapUtils.isEmpty(columnMetaMap)) {
            errors.add(new TableConstraintException("Table: '{}' not exist ", tableInfo.getTableName()));
            return;
        }

        Table table = new Table(tableInfo, columnMetaMap);
        for (TableConstraintChecker checker : checkers) {
            checkAllFieldExist(table);
            checker.checkBasicField(table);
            if (ClassUtils.isAssignable(BaseEntityWithLogicDelete.class, entityClass)) {
                checker.checkLogicDeleteField(table);
            }
        }
        errors.addAll(table.getErrors());
    }

    private String getDatabaseName() {
        try {
            return dataSource.getConnection().getCatalog();
        } catch (SQLException e) {
            throw new BaseException("Can not get connection from DataSource", e);
        }
    }

    private Map<String, List<ColumnMeta>> loadTableMetadata(List<TableMetaDataLoader> loaders) {
        String database = getDatabaseName();

        Map<String, List<ColumnMeta>> tableMetadata = null;
        for (TableMetaDataLoader metaDataLoader : loaders) {
            tableMetadata = metaDataLoader.loadTableMeta(database);
            if (tableMetadata != null) {
                break;
            }
        }
        return tableMetadata;
    }

    private List<Class<?>> getAllMappers() {
        Map<String, SqlSessionFactory> sqlSessionFactoryMap = beanFactory.getBeansOfType(SqlSessionFactory.class);
        return sqlSessionFactoryMap.values().stream()
                .flatMap(sqlSessionFactory -> sqlSessionFactory.getConfiguration().getMapperRegistry().getMappers().stream())
                .collect(toList());
    }

    /**
     * @see TableInfoHelper#getAllFields(Class)
     */
    private void checkAllFieldExist(Table table) {
        for (FieldInfo fieldInfo : table.getFieldInfoList()) {
            if (!table.containsColumn(fieldInfo.getColumn())) {
                table.addError(new TableConstraintException(table.getTableName(), fieldInfo.getColumn(), "not exist"));
            }
        }
    }

}