package com.schbrain.framework.autoconfigure.apollo;

import com.ctrip.framework.foundation.Foundation;
import com.schbrain.common.util.ApplicationName;
import com.schbrain.common.util.EnvUtils;
import com.schbrain.framework.support.spring.LoggerAwareEnvironmentPostProcessor;
import com.schbrain.framework.support.spring.defaults.DefaultPropertiesEnvironmentPostProcessor;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.*;
import static com.ctrip.framework.apollo.core.ConfigConsts.APOLLO_META_KEY;
import static com.ctrip.framework.apollo.spring.config.PropertySourcesConstants.APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED;
import static com.ctrip.framework.apollo.spring.config.PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED;

/**
 * @author liaozan
 * @since 2021/11/6
 */
public class ApolloConfigurationInitializerEnvironmentPostProcessor extends LoggerAwareEnvironmentPostProcessor implements Ordered {

    /**
     * load properties after set the default properties
     */
    public static final Integer DEFAULT_ORDER = DefaultPropertiesEnvironmentPostProcessor.DEFAULT_ORDER + 1;

    private static final String ENV_KEY = "env";

    private static Map<String, Object> INIT_PROPERTIES = new LinkedHashMap<>();

    private final ConfigurablePropertiesLoader configurablePropertiesLoader;

    public ApolloConfigurationInitializerEnvironmentPostProcessor(DeferredLogFactory deferredLogFactory, ConfigurableBootstrapContext bootstrapContext) {
        super(deferredLogFactory, bootstrapContext);
        this.configurablePropertiesLoader = new ConfigurablePropertiesLoader(deferredLogFactory, bootstrapContext);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (disabled(environment)) {
            print(APOLLO_BOOTSTRAP_ENABLED + " is disabled");
            return;
        }
        setRequiredProperty(environment);
        configurablePropertiesLoader.load(environment, application);
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    private boolean disabled(ConfigurableEnvironment environment) {
        Boolean enabled = environment.getProperty(APOLLO_BOOTSTRAP_ENABLED, Boolean.class, true);
        return Boolean.FALSE.equals(enabled);
    }

    private void setRequiredProperty(ConfigurableEnvironment environment) {
        String appId = getAppId(environment);
        setPropertyToSystem(APP_ID, appId);

        String env = getEnv(environment);
        setPropertyToSystem(ENV_KEY, env);

        String metaServerUrl = getApolloMetaServerUrl(environment, env);
        setPropertyToSystem(APOLLO_META, metaServerUrl);

        setPropertyToSystem(APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED, true);
        setPropertyToSystem(APOLLO_BOOTSTRAP_ENABLED, true);
        setPropertyToSystem(APOLLO_CACHE_FILE_ENABLE, true);
        setPropertyToSystem(APOLLO_PROPERTY_ORDER_ENABLE, true);
        // DO NOT set to true. After caching the property name, SpringBoot may not be able to bind the properties
        setPropertyToSystem(APOLLO_PROPERTY_NAMES_CACHE_ENABLE, false);
        setPropertyToSystem(APOLLO_OVERRIDE_SYSTEM_PROPERTIES, false);

        printProperties();
    }

    private void setPropertyToSystem(String key, Object value) {
        INIT_PROPERTIES.put(key, value);
        System.setProperty(key, value.toString());
    }

    private void printProperties() {
        INIT_PROPERTIES.forEach((k, v) -> print(k + " : " + v));
        INIT_PROPERTIES = null;
    }

    private void print(String message) {
        getLog().debug(message);
        System.out.println(message);
    }

    /**
     * get apollo meta server url
     *
     * @see com.ctrip.framework.foundation.internals.provider.DefaultApplicationProvider#getProperty(String, String)
     */
    private String getApolloMetaServerUrl(ConfigurableEnvironment environment, String env) {
        String fallbackKey = env + ".meta";

        // {env}.meta
        String searchKey = fallbackKey;
        String apolloUrl = environment.getProperty(searchKey);
        if (StringUtils.hasText(apolloUrl)) {
            return apolloUrl;
        }

        // apollo.meta.{env}
        searchKey = APOLLO_META_KEY + "." + env;
        apolloUrl = environment.getProperty(searchKey);
        if (StringUtils.hasText(apolloUrl)) {
            return apolloUrl;
        }

        // apollo.meta
        searchKey = APOLLO_META_KEY;
        apolloUrl = environment.getProperty(searchKey);
        if (StringUtils.hasText(apolloUrl)) {
            return apolloUrl;
        }

        // fallback
        return Foundation.getProperty(fallbackKey, null);
    }

    /**
     * get env
     *
     * @see #getApolloMetaServerUrl(org.springframework.core.env.ConfigurableEnvironment, String)
     */
    private String getEnv(ConfigurableEnvironment environment) {
        return EnvUtils.getProfile(environment);
    }

    /**
     * get AppId
     *
     * @see com.ctrip.framework.foundation.internals.provider.DefaultApplicationProvider#initAppId()
     */
    @SuppressWarnings("JavadocReference")
    private String getAppId(ConfigurableEnvironment environment) {
        if (environment.containsProperty(APP_ID)) {
            return environment.getRequiredProperty(APP_ID);
        } else {
            return ApplicationName.get(environment);
        }
    }

}