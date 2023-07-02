package com.schbrain.framework.support.spring;

import org.springframework.boot.BootstrapContextClosedEvent;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author liaozan
 * @since 2023-07-02
 */
class BootstrapContextClosedEventListener implements ApplicationListener<BootstrapContextClosedEvent> {

    private final LoggerAwareEnvironmentPostProcessor delegate;

    BootstrapContextClosedEventListener(LoggerAwareEnvironmentPostProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onApplicationEvent(BootstrapContextClosedEvent event) {
        ConfigurableBootstrapContext bootstrapContext = (ConfigurableBootstrapContext) event.getBootstrapContext();
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        delegate.onBootstrapContextClose(bootstrapContext, applicationContext);
    }

}