package com.schbrain.framework.autoconfigure.apollo.event.listener;

import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import org.apache.commons.logging.Log;
import org.springframework.core.ResolvableType;

/**
 * @author liaozan
 * @since 2023-04-28
 */
@SuppressWarnings("unchecked")
public abstract class GenericConfigLoadedEventListener<T extends ConfigurableProperties> implements ConfigLoadedEventListener {

    protected final ResolvableType propertiesType;

    protected Log log;

    public GenericConfigLoadedEventListener() {
        this.propertiesType = ResolvableType.forInstance(this).getSuperType().getGeneric(0);
    }

    @Override
    public void onApplicationEvent(ConfigLoadedEvent event) {
        if (propertiesType.isInstance(event.getConfigurableProperties())) {
            this.log = event.getDeferredLogFactory().getLog(getClass());
            this.onConfigLoaded(event, (T) event.getConfigurableProperties());
        }
    }

    protected abstract void onConfigLoaded(ConfigLoadedEvent event, T configurableProperties);

}