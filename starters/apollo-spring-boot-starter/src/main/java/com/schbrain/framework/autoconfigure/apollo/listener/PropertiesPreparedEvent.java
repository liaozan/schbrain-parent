package com.schbrain.framework.autoconfigure.apollo.listener;

import com.schbrain.common.util.properties.SchbrainMapPropertySource;
import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author liaozan
 * @since 2023-04-28
 */
@Getter
public class PropertiesPreparedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 2567291189881702459L;

    private final ConfigurableEnvironment environment;

    private final DeferredLogFactory deferredLogFactory;

    private final SchbrainMapPropertySource propertySource;

    private final SpringApplication application;

    public PropertiesPreparedEvent(ConfigurableEnvironment environment,
                                   DeferredLogFactory deferredLogFactory,
                                   SchbrainMapPropertySource propertySource,
                                   ConfigurableProperties properties,
                                   SpringApplication application) {
        super(properties);
        this.environment = environment;
        this.propertySource = propertySource;
        this.deferredLogFactory = deferredLogFactory;
        this.application = application;
    }

    public ConfigurableProperties getConfigurableProperties() {
        return (ConfigurableProperties) getSource();
    }

}