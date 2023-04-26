package com.schbrain.framework.autoconfigure.apollo.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import com.schbrain.framework.support.spring.BeanPostProcessorAdapter;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author liaozan
 * @since 2022/9/16
 */
public class ConfigurablePropertiesBeanPostProcessor extends BeanPostProcessorAdapter<ConfigurableProperties> {

    public ConfigurablePropertiesBeanPostProcessor(ConfigurableApplicationContext applicationContext) {
        this.setApplicationContext(applicationContext);
    }

    @Override
    protected ConfigurableProperties doPostProcessBeforeInstantiation(Class<ConfigurableProperties> beanClass) {
        return ConfigUtils.loadConfig(environment, beanClass);
    }

}