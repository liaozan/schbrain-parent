package com.schbrain.framework.autoconfigure.apollo.event.listener;

import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * @author liaozan
 * @since 2023-04-29
 */
public interface ConfigLoadedEventListener extends ApplicationListener<ConfigLoadedEvent>, Ordered {

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}