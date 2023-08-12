package com.schbrain.framework.autoconfigure.apollo.event.listener;

import com.schbrain.common.util.support.ConfigurableProperties;
import com.schbrain.framework.autoconfigure.apollo.event.ConfigLoadedEvent;
import org.apache.commons.logging.Log;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;

/**
 * @author liaozan
 * @since 2023-04-28
 */
@SuppressWarnings("unchecked")
public abstract class ConfigLoadedEventListenerAdaptor<T extends ConfigurableProperties> implements ConfigLoadedEventListener {

    protected final ResolvableType propertiesType;
    protected Log log;
    private T properties;

    public ConfigLoadedEventListenerAdaptor() {
        this.propertiesType = ResolvableType.forInstance(this).getSuperType().getGeneric(0);
    }

    @Override
    public void onApplicationEvent(ConfigLoadedEvent event) {
        event.getSpringApplication().addInitializers(this);
        if (propertiesType.isInstance(event.getSource()) && properties == null) {
            this.log = event.getDeferredLogFactory().getLog(getClass());
            this.properties = (T) event.getSource();
            this.onConfigLoaded(event, properties);
        }
    }

    @Override
    public final void initialize(ConfigurableApplicationContext applicationContext) {
        onApplicationContextInitialized(applicationContext, properties);
    }

    /**
     * callback when config loaded
     */
    protected abstract void onConfigLoaded(ConfigLoadedEvent event, T configurableProperties);

    /**
     * callback when application context initialized
     */
    protected abstract void onApplicationContextInitialized(ConfigurableApplicationContext context, T properties);

}
