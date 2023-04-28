package com.schbrain.framework.autoconfigure.logger.logstash;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.ContextAware;
import net.logstash.logback.LogstashFormatter;
import net.logstash.logback.composite.JsonProvider;
import net.logstash.logback.composite.LogstashVersionJsonProvider;
import net.logstash.logback.composite.loggingevent.LogLevelValueJsonProvider;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liaozan
 * @since 2022/1/4
 */
public class EnhancedLogstashFormatter extends LogstashFormatter {

    public EnhancedLogstashFormatter(ContextAware declaredOrigin) {
        super(declaredOrigin);
        configureProviders();
    }

    private void configureProviders() {
        removeUnnecessaryProviders();
        addAdditionalProviders();
    }

    private void addAdditionalProviders() {
        getProviders().addProvider(new EventDateStringValueJsonProvider());
    }

    /**
     * be careful!!! getProviders().getProviders() is a unmodifiable List
     */
    private void removeUnnecessaryProviders() {
        List<JsonProvider<ILoggingEvent>> providers = getProviders().getProviders();
        if (CollectionUtils.isEmpty(providers)) {
            return;
        }

        List<JsonProvider<ILoggingEvent>> excludeProviders = getExcludeProviders(providers);
        if (CollectionUtils.isEmpty(excludeProviders)) {
            return;
        }

        for (JsonProvider<ILoggingEvent> excludeProvider : excludeProviders) {
            getProviders().removeProvider(excludeProvider);
        }
    }

    private List<JsonProvider<ILoggingEvent>> getExcludeProviders(List<JsonProvider<ILoggingEvent>> providers) {
        List<JsonProvider<ILoggingEvent>> excludeProviders = new ArrayList<>();
        for (JsonProvider<ILoggingEvent> provider : providers) {
            if (provider instanceof LogLevelValueJsonProvider) {
                excludeProviders.add(provider);
            }
            if (provider instanceof LogstashVersionJsonProvider) {
                excludeProviders.add(provider);
            }
        }
        return excludeProviders;
    }

}