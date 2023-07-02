package com.schbrain.framework.autoconfigure.dubbo.listener;

import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.apache.dubbo.config.spring.context.event.DubboConfigInitEvent;
import org.apache.dubbo.config.spring.util.DubboBeanUtils;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.PriorityOrdered;

import java.util.Map;

import static org.apache.dubbo.config.spring.util.EnvironmentUtils.filterDubboProperties;

/**
 * @author liaozan
 * @see ReferenceAnnotationBeanPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)
 * @since 2023-05-08
 */
class DubboConfigInitEventListener implements ApplicationListener<DubboConfigInitEvent>, PriorityOrdered {

    private final ConfigurableApplicationContext applicationContext;

    DubboConfigInitEventListener(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(DubboConfigInitEvent event) {
        if (event.getApplicationContext() == applicationContext) {
            ApplicationModel applicationModel = DubboBeanUtils.getApplicationModel(applicationContext);
            ConfigManager configManager = applicationModel.getApplicationConfigManager();
            configManager.addConfigCenter(buildConfigCenterConfig());
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    private ConfigCenterConfig buildConfigCenterConfig() {
        Map<String, String> externalConfiguration = filterDubboProperties(applicationContext.getEnvironment());
        ConfigCenterConfig configCenterConfig = new ConfigCenterConfig();
        configCenterConfig.setAppExternalConfig(externalConfiguration);
        return configCenterConfig;
    }

}