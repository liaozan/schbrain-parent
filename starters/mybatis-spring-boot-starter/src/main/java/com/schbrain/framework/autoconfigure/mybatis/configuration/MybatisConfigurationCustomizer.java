package com.schbrain.framework.autoconfigure.mybatis.configuration;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.schbrain.framework.autoconfigure.mybatis.core.MybatisXmlLanguageDriver;
import com.schbrain.framework.autoconfigure.mybatis.properties.MybatisProperties;
import com.schbrain.framework.autoconfigure.mybatis.type.InstantToLongTypeHandler;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

import java.time.Instant;

/**
 * @author liaozan
 * @since 2021/11/8
 */
public class MybatisConfigurationCustomizer implements ConfigurationCustomizer {

    private final MybatisProperties mybatisProperties;

    public MybatisConfigurationCustomizer(MybatisProperties mybatisProperties) {
        this.mybatisProperties = mybatisProperties;
    }

    @Override
    public void customize(MybatisConfiguration configuration) {
        configuration.setCacheEnabled(false);
        configuration.setDefaultEnumTypeHandler(EnumOrdinalTypeHandler.class);
        configuration.setUseActualParamName(true);
        configuration.setMapUnderscoreToCamelCase(true);
        // set MybatisXmlLanguageDriver default to support deleteVersion field fill
        configuration.setDefaultScriptingLanguage(MybatisXmlLanguageDriver.class);
        if (mybatisProperties.isConvertInstantToLong()) {
            configuration.getTypeHandlerRegistry().register(Instant.class, new InstantToLongTypeHandler());
        }
    }

}