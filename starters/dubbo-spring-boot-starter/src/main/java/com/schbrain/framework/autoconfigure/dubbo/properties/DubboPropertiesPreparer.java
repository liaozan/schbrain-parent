package com.schbrain.framework.autoconfigure.dubbo.properties;

import com.alibaba.fastjson2.JSONFactory;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.ApplicationName;
import com.schbrain.framework.autoconfigure.apollo.util.ConfigUtils;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.spring.ConfigCenterBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.dubbo.config.ConfigKeys.DUBBO_SCAN_BASE_PACKAGES;

/**
 * @author liaozan
 * @since 2021/10/10
 */
public class DubboPropertiesPreparer implements EnvironmentPostProcessor, Ordered {

    public static final String DUBBO_APPLICATION_NAME = "dubbo.application.name";

    public static final Integer DEFAULT_ORDER = Ordered.LOWEST_PRECEDENCE;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        DubboProperties dubboProperties = ConfigUtils.loadConfig(environment, DubboProperties.class);
        addAdditionalProperties(environment, application, dubboProperties);
        setUpConfigCenter(dubboProperties);
        JSONFactory.setUseJacksonAnnotation(false);
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    private void setUpConfigCenter(DubboProperties dubboProperties) {
        ConfigCenterBean configCenterConfig = buildConfigCenter(dubboProperties);
        DubboBootstrap.getInstance().configCenter(configCenterConfig);
    }

    private ConfigCenterBean buildConfigCenter(DubboProperties dubboProperties) {
        ConfigCenterBean configCenter = new ConfigCenterBean();
        configCenter.setExternalConfig(dubboProperties.getExternalConfigurations());
        return configCenter;
    }

    private void addAdditionalProperties(ConfigurableEnvironment environment, SpringApplication application, DubboProperties dubboProperties) {
        Map<String, String> configurations = dubboProperties.getExternalConfigurations();
        configurations.put(DUBBO_SCAN_BASE_PACKAGES, getBasePackage(application));
        if (!configurations.containsKey(DUBBO_APPLICATION_NAME)) {
            configurations.put(DUBBO_APPLICATION_NAME, ApplicationName.get(environment));
        }
        Map<String, Object> properties = new LinkedHashMap<>(dubboProperties.getExternalConfigurations());
        ConfigUtils.addToEnvironment(environment, dubboProperties.getName(), properties);
    }

    private String getBasePackage(SpringApplication application) {
        return application.getAllSources()
                .stream()
                .filter(Class.class::isInstance)
                .map(source -> (Class<?>) source)
                .map(Class::getPackage)
                .map(Package::getName)
                .findFirst()
                .orElseThrow(() -> new BaseException("should never go here"));
    }

}