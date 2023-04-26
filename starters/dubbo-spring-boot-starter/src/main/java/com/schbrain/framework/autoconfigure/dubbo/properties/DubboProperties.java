package com.schbrain.framework.autoconfigure.dubbo.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.dubbo.config.spring.util.EnvironmentUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * used to fetch dubbo.* config from remote
 *
 * @author liaozan
 * @since 2021/12/6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "dubbo")
public class DubboProperties extends ConfigurableProperties {

    private Map<String, String> externalConfigurations;

    @Override
    public String getDefaultNamespace() {
        return "dubbo-common";
    }

    @Override
    @SuppressWarnings("unchecked")
    public DubboProperties bindOrCreate(ConfigurableEnvironment environment, boolean afterMerge) {
        DubboProperties dubboProperties = super.bindOrCreate(environment, afterMerge);
        if (afterMerge) {
            Map<String, String> externalConfigurations = new LinkedHashMap<>(EnvironmentUtils.filterDubboProperties(environment));
            Map<String, String> configuredProperties = dubboProperties.getExternalConfigurations();
            if (configuredProperties == null) {
                configuredProperties = new LinkedHashMap<>();
            }
            externalConfigurations.putAll(configuredProperties);
            dubboProperties.setExternalConfigurations(externalConfigurations);
        }
        return dubboProperties;
    }

}