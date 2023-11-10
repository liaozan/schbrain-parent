package com.schbrain.framework.autoconfigure.dubbo.listener;

import com.schbrain.framework.support.spring.OnceApplicationContextEventListener;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.context.ConfigManager;
import org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.apache.dubbo.config.spring.context.event.DubboConfigInitEvent;
import org.apache.dubbo.config.spring.util.DubboBeanUtils;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

import static org.apache.dubbo.config.spring.util.EnvironmentUtils.filterDubboProperties;

/**
 * @author liaozan
 * @see ReferenceAnnotationBeanPostProcessor#postProcessBeanFactory(ConfigurableListableBeanFactory)
 * @since 2023-05-08
 */
class DubboConfigInitEventListener extends OnceApplicationContextEventListener<DubboConfigInitEvent> implements PriorityOrdered {

    private final ConfigurableEnvironment environment;

    DubboConfigInitEventListener(ConfigurableApplicationContext applicationContext) {
        super(applicationContext);
        this.environment = applicationContext.getEnvironment();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    protected void onEvent(DubboConfigInitEvent event) {
        ApplicationModel applicationModel = DubboBeanUtils.getApplicationModel(getApplicationContext());
        ConfigManager configManager = applicationModel.getApplicationConfigManager();
        configManager.addConfigCenter(buildConfigCenterConfig());
    }

    private ConfigCenterConfig buildConfigCenterConfig() {
        Map<String, String> externalConfiguration = filterDubboProperties(environment);
        return buildConfigCenterConfig(externalConfiguration);
    }

    private ConfigCenterConfig buildConfigCenterConfig(Map<String, String> externalConfiguration) {
        ConfigCenterConfig configCenterConfig = new ConfigCenterConfig();
        configCenterConfig.setAppExternalConfig(externalConfiguration);
        return configCenterConfig;
    }

}
