package com.schbrain.framework.autoconfigure.dubbo.listener;

import com.schbrain.common.util.ApplicationName;
import com.schbrain.common.util.properties.OrderedMapPropertySource;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.GenericConfigLoadedEventListener;
import com.schbrain.framework.autoconfigure.dubbo.properties.DubboProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.apache.dubbo.config.ConfigKeys.DUBBO_SCAN_BASE_PACKAGES;

/**
 * @author liaozan
 * @since 2023-04-28
 */
public class DubboConfigLoadedEventListener extends GenericConfigLoadedEventListener<DubboProperties> {

    public static final String DUBBO_APPLICATION_NAME = "dubbo.application.name";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addApplicationListener(new DubboConfigInitEventListener(applicationContext));
    }

    @Override
    protected void onConfigLoaded(ConfigLoadedEvent event, DubboProperties properties) {
        addRequiredProperties(event.getEnvironment(), event.getSpringApplication(), event.getPropertySource());
    }

    private void addRequiredProperties(ConfigurableEnvironment environment, SpringApplication application, OrderedMapPropertySource propertySource) {
        if (!propertySource.containsProperty(DUBBO_SCAN_BASE_PACKAGES)) {
            propertySource.addProperty(DUBBO_SCAN_BASE_PACKAGES, getBasePackage(application));
        }
        if (!propertySource.containsProperty(DUBBO_APPLICATION_NAME)) {
            propertySource.addProperty(DUBBO_APPLICATION_NAME, ApplicationName.get(environment));
        }
    }

    private String getBasePackage(SpringApplication application) {
        return application.getMainApplicationClass().getPackage().getName();
    }

}