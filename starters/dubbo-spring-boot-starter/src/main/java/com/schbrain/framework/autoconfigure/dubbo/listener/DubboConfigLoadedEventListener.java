package com.schbrain.framework.autoconfigure.dubbo.listener;

import com.schbrain.common.util.properties.OrderedMapPropertySource;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.GenericConfigLoadedEventListener;
import com.schbrain.framework.autoconfigure.dubbo.initializer.DubboValidationInitializer;
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
        DubboValidationInitializer.initialize(event.getPropertySource());
    }

    private void addRequiredProperties(ConfigurableEnvironment environment, SpringApplication application, OrderedMapPropertySource propertySource) {
        if (!environment.containsProperty(DUBBO_SCAN_BASE_PACKAGES)) {
            propertySource.addProperty(DUBBO_SCAN_BASE_PACKAGES, getBasePackage(application));
        }
    }

    private String getBasePackage(SpringApplication application) {
        return application.getMainApplicationClass().getPackage().getName();
    }

}