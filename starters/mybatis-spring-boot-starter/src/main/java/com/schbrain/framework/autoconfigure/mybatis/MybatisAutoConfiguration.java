package com.schbrain.framework.autoconfigure.mybatis;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.schbrain.framework.autoconfigure.mybatis.biz.BizIdInjectInterceptor;
import com.schbrain.framework.autoconfigure.mybatis.configuration.MybatisConfigurationCustomizer;
import com.schbrain.framework.autoconfigure.mybatis.configuration.MybatisPlusGlobalConfigCustomizer;
import com.schbrain.framework.autoconfigure.mybatis.datasource.DataSourceConnectionPostProcessor;
import com.schbrain.framework.autoconfigure.mybatis.datasource.customizer.DataSourceCustomizer;
import com.schbrain.framework.autoconfigure.mybatis.datasource.customizer.DefaultDataSourceCustomizer;
import com.schbrain.framework.autoconfigure.mybatis.datasource.extractor.DataSourcePropertiesExtractor;
import com.schbrain.framework.autoconfigure.mybatis.datasource.extractor.DruidDataSourcePropertiesExtractor;
import com.schbrain.framework.autoconfigure.mybatis.datasource.extractor.HikariDataSourcePropertiesExtractor;
import com.schbrain.framework.autoconfigure.mybatis.listener.TableConstraintCheckerBean;
import com.schbrain.framework.autoconfigure.mybatis.properties.DataSourceConnectionProperties;
import com.schbrain.framework.autoconfigure.mybatis.properties.DataSourceProperties;
import com.schbrain.framework.autoconfigure.mybatis.properties.MybatisProperties;
import com.schbrain.framework.autoconfigure.mybatis.sql.injector.DefaultMethodSqlInjector;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author liaozan
 * @since 2021/10/14
 */
@AutoConfiguration(before = MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties({DataSourceProperties.class, MybatisProperties.class, DataSourceConnectionProperties.class})
@Import({HikariDataSourcePropertiesExtractor.class, DruidDataSourcePropertiesExtractor.class})
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class MybatisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor(MybatisProperties mybatisProperties,
                                                         ObjectProvider<BlockAttackInnerInterceptor> attackInterceptor,
                                                         ObjectProvider<PaginationInnerInterceptor> paginationInterceptor,
                                                         ObjectProvider<BizIdInjectInterceptor> bizIdInjectInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (mybatisProperties.isAddBlockAttackInterceptor()) {
            interceptor.addInnerInterceptor(attackInterceptor.getIfUnique(BlockAttackInnerInterceptor::new));
        }
        if (mybatisProperties.isAddPageInterceptor()) {
            interceptor.addInnerInterceptor(paginationInterceptor.getIfUnique(PaginationInnerInterceptor::new));
        }
        bizIdInjectInterceptor.ifUnique(interceptor::addInnerInterceptor);
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ISqlInjector defaultSqlInjector() {
        return new DefaultMethodSqlInjector();
    }

    @Bean
    @ConditionalOnMissingBean
    public BizIdInjectInterceptor bizIdInjectInterceptor() {
        return new BizIdInjectInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSourceCustomizer dataSourceCustomizer(List<DataSourcePropertiesExtractor> extractors) {
        return new DefaultDataSourceCustomizer(extractors);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnSingleCandidate(DataSource.class)
    public TableConstraintCheckerBean constraintCheckInitializer(DataSource dataSource, MybatisProperties mybatisProperties) {
        return new TableConstraintCheckerBean(dataSource, mybatisProperties);
    }

    @Bean
    public MybatisPlusPropertiesCustomizer globalConfigCustomizer() {
        return new MybatisPlusGlobalConfigCustomizer();
    }

    @Bean
    public MybatisConfigurationCustomizer configurationCustomizer(MybatisProperties mybatisProperties) {
        return new MybatisConfigurationCustomizer(mybatisProperties);
    }

    @Bean
    public DataSourceConnectionPostProcessor dataSourceConnectionPostProcessor(ObjectProvider<DataSourceCustomizer> dataSourceCustomizers,
                                                                               ObjectProvider<DataSourceConnectionProperties> connectionProperties) {
        return new DataSourceConnectionPostProcessor(dataSourceCustomizers, connectionProperties);
    }

}