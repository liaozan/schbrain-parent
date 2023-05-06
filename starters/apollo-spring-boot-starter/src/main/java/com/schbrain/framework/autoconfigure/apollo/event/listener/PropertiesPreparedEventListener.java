package com.schbrain.framework.autoconfigure.apollo.event.listener;

import com.schbrain.framework.autoconfigure.apollo.event.PropertiesPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * @author liaozan
 * @since 2023-04-29
 */
public interface PropertiesPreparedEventListener extends ApplicationListener<PropertiesPreparedEvent>, Ordered {

    @Override
    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}