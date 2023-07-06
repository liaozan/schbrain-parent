package com.schbrain.framework.autoconfigure.apollo.event;

import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.config.OrderedMapPropertySource;
import lombok.Getter;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author liaozan
 * @since 2023-04-28
 */
@Getter
public class ConfigLoadedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 2567291189881702459L;

    private final ConfigurableEnvironment environment;
    private final DeferredLogFactory deferredLogFactory;
    private final OrderedMapPropertySource propertySource;
    private final SpringApplication springApplication;
    private final ConfigurableBootstrapContext bootstrapContext;

    public ConfigLoadedEvent(ConfigurableEnvironment environment,
                             DeferredLogFactory deferredLogFactory,
                             OrderedMapPropertySource propertySource,
                             ConfigurableProperties properties,
                             SpringApplication springApplication,
                             ConfigurableBootstrapContext bootstrapContext) {
        super(properties);
        this.environment = environment;
        this.propertySource = propertySource;
        this.deferredLogFactory = deferredLogFactory;
        this.springApplication = springApplication;
        this.bootstrapContext = bootstrapContext;
    }

    @Override
    public ConfigurableProperties getSource() {
        return (ConfigurableProperties) super.getSource();
    }

}