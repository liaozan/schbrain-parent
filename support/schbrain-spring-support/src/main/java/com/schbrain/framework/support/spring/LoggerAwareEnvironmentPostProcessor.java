package com.schbrain.framework.support.spring;

import lombok.Getter;
import org.apache.commons.logging.Log;
import org.springframework.boot.BootstrapContext;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author liaozan
 * @since 2021/11/22
 */
@Getter
public abstract class LoggerAwareEnvironmentPostProcessor implements EnvironmentPostProcessor {

    protected final Log log;
    protected final DeferredLogFactory deferredLogFactory;
    protected final ConfigurableBootstrapContext bootstrapContext;

    public LoggerAwareEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        this.log = logFactory.getLog(getClass());
        this.bootstrapContext = bootstrapContext;
        this.deferredLogFactory = logFactory;
        this.bootstrapContext.addCloseListener(event -> onBootstrapContextClose(event.getBootstrapContext(), event.getApplicationContext()));
    }

    protected void onBootstrapContextClose(BootstrapContext bootstrapContext, ConfigurableApplicationContext applicationContext) {

    }

}