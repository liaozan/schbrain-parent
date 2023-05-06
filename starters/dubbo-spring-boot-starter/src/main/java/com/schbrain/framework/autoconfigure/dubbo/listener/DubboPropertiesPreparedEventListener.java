package com.schbrain.framework.autoconfigure.dubbo.listener;

import com.alibaba.fastjson2.JSONFactory;
import com.google.common.collect.Maps;
import com.schbrain.common.util.ApplicationName;
import com.schbrain.framework.autoconfigure.apollo.event.PropertiesPreparedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.GenericPropertiesPreparedEventListener;
import com.schbrain.framework.autoconfigure.dubbo.properties.DubboProperties;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.spring.ConfigCenterBean;
import org.apache.dubbo.config.spring.util.EnvironmentUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;
import java.util.SortedMap;

import static org.apache.dubbo.config.ConfigKeys.DUBBO_SCAN_BASE_PACKAGES;

/**
 * @author liaozan
 * @since 2023-04-28
 */
public class DubboPropertiesPreparedEventListener extends GenericPropertiesPreparedEventListener<DubboProperties> {

    public static final String DUBBO_APPLICATION_NAME = "dubbo.application.name";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void onPropertiesPrepared(PropertiesPreparedEvent event, DubboProperties properties) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Map<String, String> requiredProperties = collectRequiredProperties(environment, event.getApplication());
        event.getPropertySource().addProperties(requiredProperties);
        injectDubboProperties(environment);
    }

    private void injectDubboProperties(ConfigurableEnvironment environment) {
        JSONFactory.setUseJacksonAnnotation(false);
        SortedMap<String, String> dubboProperties = EnvironmentUtils.filterDubboProperties(environment);
        ConfigCenterBean configCenterBean = new ConfigCenterBean();
        configCenterBean.setExternalConfig(dubboProperties);
        DubboBootstrap.getInstance().configCenter(configCenterBean);
    }

    private Map<String, String> collectRequiredProperties(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, String> dubboRequiredProperties = Maps.newLinkedHashMapWithExpectedSize(2);
        dubboRequiredProperties.put(DUBBO_SCAN_BASE_PACKAGES, getBasePackage(application));
        dubboRequiredProperties.put(DUBBO_APPLICATION_NAME, ApplicationName.get(environment));
        return dubboRequiredProperties;
    }

    private String getBasePackage(SpringApplication application) {
        return application.getMainApplicationClass().getPackage().getName();
    }

}