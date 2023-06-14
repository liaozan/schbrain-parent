package com.schbrain.framework.support.spring.startup;

import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.metrics.ApplicationStartup;

/**
 * @author liaozan
 * @since 2023-06-12
 */
public class BufferingApplicationStartupApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext.getApplicationStartup() == ApplicationStartup.DEFAULT) {
            applicationContext.setApplicationStartup(new BufferingApplicationStartup(Integer.MAX_VALUE));
        }
    }

}