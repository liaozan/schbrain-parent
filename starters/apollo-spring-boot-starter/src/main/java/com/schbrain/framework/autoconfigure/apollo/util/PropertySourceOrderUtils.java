package com.schbrain.framework.autoconfigure.apollo.util;

import com.schbrain.framework.autoconfigure.apollo.properties.ApolloProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ctrip.framework.apollo.spring.config.PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME;
import static com.ctrip.framework.apollo.spring.config.PropertySourcesConstants.APOLLO_PROPERTY_SOURCE_NAME;

/**
 * @author liaozan
 * @since 2022/4/19
 */
public class PropertySourceOrderUtils {

    private static final Set<String> APOLLO_PROPERTIES = Set.of(APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME, APOLLO_PROPERTY_SOURCE_NAME);

    /**
     * Adjust {@link MutablePropertySources} according to the
     * {@link ApolloProperties#isRemoteFirst()} to ensure the configuration properties work as expected
     */
    public static void adjustPropertySourceOrder(ConfigurableEnvironment environment) {
        MutablePropertySources mutablePropertySources = environment.getPropertySources();

        List<PropertySource<?>> remotePropertySources = mutablePropertySources.stream()
                .filter(propertySource -> APOLLO_PROPERTIES.contains(propertySource.getName()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(remotePropertySources)) {
            return;
        }

        ApolloProperties apolloProperties = ApolloProperties.get(environment);
        for (PropertySource<?> remotePropertySource : remotePropertySources) {
            mutablePropertySources.remove(remotePropertySource.getName());
            if (apolloProperties.isRemoteFirst()) {
                mutablePropertySources.addFirst(remotePropertySource);
            } else {
                mutablePropertySources.addLast(remotePropertySource);
            }
        }

        // Make sure the default configurations always in the end
        DefaultPropertiesPropertySource.moveToEnd(environment);
    }

}