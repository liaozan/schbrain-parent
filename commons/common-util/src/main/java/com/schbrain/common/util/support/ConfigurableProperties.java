package com.schbrain.common.util.support;

import com.schbrain.common.util.ConfigurationPropertiesUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.*;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author liaozan
 * @since 2022/1/10
 */
@Data
public abstract class ConfigurableProperties implements Ordered {

    /**
     * get the namespace of remote config
     */
    public abstract String getDefaultNamespace();

    /**
     * bind properties
     */
    public ConfigurableProperties bind(ConfigurableEnvironment environment) {
        return Binder.get(environment, bindHandler()).bindOrCreate(getPropertiesPrefix(), Bindable.ofInstance(this));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * the prefix of properties
     */
    protected String getPropertiesPrefix() {
        ConfigurationProperties annotation = getClass().getAnnotation(ConfigurationProperties.class);
        if (annotation == null) {
            String className = ConfigurationProperties.class.getName();
            String errorDetail = getClass().getSimpleName() + " must annotated @" + className + " or overwrite getPrefix method";
            throw new IllegalStateException(errorDetail);
        }
        return ConfigurationPropertiesUtils.getPrefix(getClass());
    }

    /**
     * get the {@link org.springframework.boot.context.properties.bind.BindHandler} for bind
     */
    protected BindHandler bindHandler() {
        return BindHandler.DEFAULT;
    }

}