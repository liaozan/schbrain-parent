package com.schbrain.framework.autoconfigure.mybatis.configuration;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig;

/**
 * 当前类在 mybatis-plus 自动配置之前初始化,所以这里只能设置 {@link GlobalConfig} 一些基本属性,类里的其他对象还没进行初始化
 * <p>
 * 如果设置了其他属性，也会在 {@link com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration} 中被覆盖
 * <p>
 * 例如
 * <ul>
 *     <li>{@link com.baomidou.mybatisplus.core.handlers.MetaObjectHandler}</li>
 *     <li>{@link com.baomidou.mybatisplus.core.incrementer.IKeyGenerator}</li>
 *     <li>{@link com.baomidou.mybatisplus.core.injector.ISqlInjector}</li>
 *     <li>{@link com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator}</li>
 * </ul>
 * <p>
 * 如果需要设置其他属性,可以往容器里注入{@link com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer}
 *
 * @author liaozan
 * @see MybatisConfigurationCustomizer
 * @since 2021/11/3
 */
public class MybatisPlusGlobalConfigCustomizer implements MybatisPlusPropertiesCustomizer {

    @Override
    public void customize(MybatisPlusProperties properties) {
        GlobalConfig globalConfig = properties.getGlobalConfig();
        globalConfig.setBanner(false);

        DbConfig dbConfig = globalConfig.getDbConfig();
        dbConfig.setIdType(IdType.AUTO);
        dbConfig.setInsertStrategy(FieldStrategy.NOT_NULL);
        dbConfig.setUpdateStrategy(FieldStrategy.NOT_NULL);
        dbConfig.setWhereStrategy(FieldStrategy.NOT_NULL);
    }

}