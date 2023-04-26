package com.schbrain.framework.support.spring;

import cn.hutool.core.lang.Singleton;
import org.springframework.boot.BootstrapContextClosedEvent;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liaozan
 * @since 2021/11/22
 */
public class BootstrapContextListenerComposite implements ApplicationListener<BootstrapContextClosedEvent> {

    private final List<EnvironmentPostProcessorAdapter> adapters = new ArrayList<>();

    public static BootstrapContextListenerComposite getInstance() {
        return Singleton.get(BootstrapContextListenerComposite.class);
    }

    public void addListener(EnvironmentPostProcessorAdapter adapter) {
        if (adapters.contains(adapter)) {
            return;
        }
        adapters.add(adapter);
    }

    @Override
    public void onApplicationEvent(BootstrapContextClosedEvent event) {
        if (adapters.isEmpty()) {
            return;
        }

        AnnotationAwareOrderComparator.sort(adapters);

        ConfigurableBootstrapContext bootstrapContext = (ConfigurableBootstrapContext) event.getBootstrapContext();
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();

        for (EnvironmentPostProcessorAdapter adapter : adapters) {
            adapter.onBootstrapContextClosed(bootstrapContext, applicationContext);
        }
    }

}
