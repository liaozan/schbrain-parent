package com.schbrain.framework.autoconfigure.apollo.listener;

import com.schbrain.common.util.support.ConfigurableProperties;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author liaozan
 * @since 2023-04-28
 */
@SuppressWarnings("unchecked")
public class PropertiesPreparedEventListenerAdapter<T extends ConfigurableProperties> implements PropertiesPreparedEventListener {

    private final Class<T> propertyType;

    public PropertiesPreparedEventListenerAdapter() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        this.propertyType = (Class<T>) actualTypeArguments[0];
    }

    @Override
    public void onApplicationEvent(PropertiesPreparedEvent event) {
        if (event.getConfigurableProperties().getClass() == propertyType) {
            onPropertiesPrepared(event, (T) event.getConfigurableProperties());
        }
    }

    protected void onPropertiesPrepared(PropertiesPreparedEvent event, T configurableProperties) {

    }

}