package com.schbrain.framework.support.spring;

import lombok.Getter;
import org.apache.commons.logging.Log;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author liaozan
 * @since 2021/11/22
 */
@Getter
public abstract class EnvironmentPostProcessorAdapter implements EnvironmentPostProcessor {

    private final Log log;
    private final DeferredLogFactory deferredLogFactory;
    private final ConfigurableBootstrapContext bootstrapContext;

    public EnvironmentPostProcessorAdapter(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        this.log = logFactory.getLog(getClass());
        this.bootstrapContext = bootstrapContext;
        this.deferredLogFactory = logFactory;
        this.addListener(this.bootstrapContext);
    }

    protected void onBootstrapContextClosed(ConfigurableBootstrapContext bootstrapContext, ConfigurableApplicationContext applicationContext) {
        onBootstrapContextClosed(applicationContext);
    }

    protected void onBootstrapContextClosed(ConfigurableApplicationContext applicationContext) {

    }

    private void addListener(ConfigurableBootstrapContext bootstrapContext) {
        BootstrapContextListenerComposite listener = BootstrapContextListenerComposite.getInstance();
        listener.addListener(this);
        bootstrapContext.addCloseListener(listener);
    }

}