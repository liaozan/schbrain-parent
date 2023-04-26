package com.schbrain.common.util.support;

import com.schbrain.common.util.ConfigurationPropertiesUtils;
import lombok.Data;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

import java.beans.Introspector;

/**
 * <b> WARNING!!! </b>
 * <p>
 * If you want to use a subclass of this class before {@link BeanPostProcessor},
 * please load it through {@link com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils}
 *
 * @author liaozan
 * @since 2022/1/10
 */
@Data
public abstract class ConfigurableProperties {

    /**
     * the namespace of remote config
     */
    protected String namespace = getDefaultNamespace();

    /**
     * the prefix of properties
     */
    protected String prefix = getPossiblePrefix();

    /**
     * the name of propertySource
     */
    protected String name = Introspector.decapitalize(getClass().getSimpleName());

    public String getDefaultNamespace() {
        return "application";
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T extends ConfigurableProperties> T bindOrCreate(ConfigurableEnvironment environment, boolean afterMerge) {
        return (T) Binder.get(environment, bindHandler()).bindOrCreate(getPrefix(), getClass());
    }

    protected BindHandler bindHandler() {
        return BindHandler.DEFAULT;
    }

    private String getPossiblePrefix() {
        ConfigurationProperties annotation = getClass().getAnnotation(ConfigurationProperties.class);
        if (annotation == null) {
            String className = ConfigurationProperties.class.getName();
            String errorDetail = getClass().getSimpleName() + " must annotated @" + className + " or overwrite getPrefix method";
            throw new IllegalStateException(errorDetail);
        }
        return ConfigurationPropertiesUtils.getPrefix(getClass());
    }

}