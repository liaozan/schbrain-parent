package com.schbrain.framework.autoconfigure.dubbo.env;

import cn.hutool.core.text.StrFormatter;
import com.google.common.collect.Maps;
import com.schbrain.common.util.EnvUtils;
import com.schbrain.framework.support.spring.LoggerAwareEnvironmentPostProcessor;
import org.springframework.boot.*;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;

import java.util.Map;

/**
 * @author liaozan
 * @since 2023/7/18
 */
public class DubboEnvironmentPostProcessor extends LoggerAwareEnvironmentPostProcessor {

    private static final String DUBBO_REGISTER_KEY = "dubbo.registry.register";

    public DubboEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        super(logFactory, bootstrapContext);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> defaultProperties = Maps.newHashMapWithExpectedSize(1);
        configureDubboRegistrationIfPresent(environment, defaultProperties);
        DefaultPropertiesPropertySource.addOrMerge(defaultProperties, environment.getPropertySources());
    }

    private void configureDubboRegistrationIfPresent(ConfigurableEnvironment environment, Map<String, Object> defaultProperties) {
        if (!dubboInClassPath()) {
            return;
        }
        if (EnvUtils.runningOnCloudPlatform(environment)) {
            return;
        }
        if (!environment.containsProperty(DUBBO_REGISTER_KEY)) {
            log.warn(StrFormatter.format("Not running on CloudPlatform, {} is set to false by default", DUBBO_REGISTER_KEY));
            log.warn(StrFormatter.format("If you want force to register with Dubbo Registry, set {} = true", DUBBO_REGISTER_KEY));
            defaultProperties.put(DUBBO_REGISTER_KEY, false);
        }
    }

    private boolean dubboInClassPath() {
        return ClassUtils.isPresent("org.apache.dubbo.config.bootstrap.DubboBootstrap", getClass().getClassLoader());
    }

}
