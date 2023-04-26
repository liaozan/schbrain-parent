package com.schbrain.framework.autoconfigure.apollo.properties;

import com.ctrip.framework.foundation.Foundation;
import com.schbrain.common.util.ApplicationName;
import com.schbrain.common.util.EnvUtils;
import com.schbrain.framework.support.spring.EnvironmentPostProcessorAdapter;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.*;
import static com.ctrip.framework.apollo.core.ConfigConsts.APOLLO_META_KEY;
import static com.ctrip.framework.apollo.spring.config.PropertySourcesConstants.*;

/**
 * @author liaozan
 * @since 2021/11/6
 */
public class ApolloPropertiesPreparer extends EnvironmentPostProcessorAdapter implements Ordered {

    // get properties after configData loaded
    public static final Integer ORDER = ConfigDataEnvironmentPostProcessor.ORDER + 1;
    public static final String ENV_KEY = "env";
    private static final Map<String, Object> INIT_PROPERTIES = new LinkedHashMap<>();

    public ApolloPropertiesPreparer(DeferredLogFactory deferredLogFactory, ConfigurableBootstrapContext bootstrapContext) {
        super(deferredLogFactory, bootstrapContext);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (disabled(environment)) {
            print("apollo is disabled");
            return;
        }
        setRequiredProperty(environment);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    protected void onBootstrapContextClosed(ConfigurableApplicationContext context) {
        context.getBeanFactory().addBeanPostProcessor(new ConfigurablePropertiesBeanPostProcessor(context));
    }

    private boolean disabled(ConfigurableEnvironment environment) {
        Boolean enabled = environment.getProperty(APOLLO_BOOTSTRAP_ENABLED, Boolean.class, true);
        return Boolean.FALSE.equals(enabled);
    }

    private void setRequiredProperty(ConfigurableEnvironment environment) {
        String appId = getAppId(environment);
        saveProperty(APP_ID, appId);

        String env = getEnv(environment);
        saveProperty(ENV_KEY, env);

        String apolloUrl = getApolloUrl(environment, env);
        saveProperty(APOLLO_META, apolloUrl);

        saveProperty(APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED, true);
        saveProperty(APOLLO_BOOTSTRAP_ENABLED, true);
        saveProperty(APOLLO_PROPERTY_ORDER_ENABLE, true);
        saveProperty(APOLLO_PROPERTY_NAMES_CACHE_ENABLE, true);

        printProperties();
    }

    private void saveProperty(String key, Object value) {
        INIT_PROPERTIES.put(key, value);
        System.setProperty(key, value.toString());
    }

    private void printProperties() {
        INIT_PROPERTIES.forEach((k, v) -> print(k + " : " + v));
    }

    private void print(String message) {
        getLog().debug(message);
        System.out.println(message);
    }

    private String getApolloUrl(ConfigurableEnvironment environment, String env) {
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

    private String getEnv(ConfigurableEnvironment environment) {
        return EnvUtils.getProfile(environment);
    }

    private String getAppId(ConfigurableEnvironment environment) {
        String appId;
        if (environment.containsProperty(APP_ID)) {
            appId = environment.getRequiredProperty(APP_ID);
        } else {
            appId = ApplicationName.get(environment);
        }
        return appId;
    }

}