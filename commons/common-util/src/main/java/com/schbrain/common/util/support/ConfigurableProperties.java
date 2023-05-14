package com.schbrain.common.util.support;

import com.schbrain.common.util.ConfigurationPropertiesUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author liaozan
 * @since 2022/1/10
 */
public interface ConfigurableProperties extends Ordered {

    /**
     * get the namespace of remote config
     */
    String getNamespace();

    /**
     * bind properties
     */
    default ConfigurableProperties bind(ConfigurableEnvironment environment) {
        return Binder.get(environment).bindOrCreate(getPropertiesPrefix(), Bindable.ofInstance(this));
    }

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * the prefix of properties
     */
    private String getPropertiesPrefix() {
        ConfigurationProperties annotation = getClass().getAnnotation(ConfigurationProperties.class);
        if (annotation == null) {
            String className = ConfigurationProperties.class.getName();
            String errorDetail = getClass().getSimpleName() + " must annotated @" + className + " or overwrite getPrefix method";
            throw new IllegalStateException(errorDetail);
        }
        return ConfigurationPropertiesUtils.getPrefix(getClass());
    }

}