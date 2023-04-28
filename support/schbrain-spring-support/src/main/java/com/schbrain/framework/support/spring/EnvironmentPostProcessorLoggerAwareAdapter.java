package com.schbrain.framework.support.spring;

import lombok.Getter;
import org.apache.commons.logging.Log;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;

/**
 * @author liaozan
 * @since 2021/11/22
 */
@Getter
public abstract class EnvironmentPostProcessorLoggerAwareAdapter implements EnvironmentPostProcessor {

    private final Log log;
    private final DeferredLogFactory deferredLogFactory;
    private final ConfigurableBootstrapContext bootstrapContext;

    public EnvironmentPostProcessorLoggerAwareAdapter(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        this.log = logFactory.getLog(getClass());
        this.bootstrapContext = bootstrapContext;
        this.deferredLogFactory = logFactory;
    }

}