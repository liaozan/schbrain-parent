package com.schbrain.framework.autoconfigure.dubbo.listener;

import com.schbrain.framework.autoconfigure.apollo.config.OrderedMapPropertySource;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import com.schbrain.framework.autoconfigure.apollo.event.listener.ConfigLoadedEventListenerAdaptor;
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
public class DubboConfigLoadedEventListener extends ConfigLoadedEventListenerAdaptor<DubboProperties> {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void onConfigLoaded(ConfigLoadedEvent event, DubboProperties properties) {
        addRequiredProperties(event.getEnvironment(), event.getSpringApplication(), event.getPropertySource());
    }

    @Override
    protected void onApplicationContextInitialized(ConfigurableApplicationContext context, DubboProperties properties) {
        context.addApplicationListener(new DubboConfigInitEventListener(context));
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
