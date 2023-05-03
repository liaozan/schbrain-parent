package com.schbrain.framework.autoconfigure.apollo.listener;

import com.schbrain.common.util.support.ConfigurableProperties;
import org.apache.commons.logging.Log;
import org.springframework.core.ResolvableType;

/**
 * @author liaozan
 * @since 2023-04-28
 */
@SuppressWarnings("unchecked")
public abstract class GenericPropertiesPreparedEventListener<T extends ConfigurableProperties> implements PropertiesPreparedEventListener {

    protected final ResolvableType propertiesType;

    protected Log log;

    public GenericPropertiesPreparedEventListener() {
        this.propertiesType = ResolvableType.forInstance(this).getSuperType().getGeneric(0);
    }

    @Override
    public void onApplicationEvent(PropertiesPreparedEvent event) {
        if (propertiesType.isInstance(event.getConfigurableProperties())) {
            this.log = event.getDeferredLogFactory().getLog(getClass());
            this.onPropertiesPrepared(event, (T) event.getConfigurableProperties());
        }
    }

    protected abstract void onPropertiesPrepared(PropertiesPreparedEvent event, T configurableProperties);

}