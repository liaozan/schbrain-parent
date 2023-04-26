package com.schbrain.framework.support.spring.defaults;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ArrayUtil;
import com.schbrain.common.constants.DateTimeFormatters;
import com.schbrain.common.util.EnvUtils;
import com.schbrain.common.util.PortUtils;
import com.schbrain.framework.support.spring.EnvironmentPostProcessorAdapter;
import org.springframework.boot.*;
import org.springframework.boot.actuate.autoconfigure.health.HealthProperties.Show;
import org.springframework.boot.actuate.info.InfoPropertiesInfoContributor.Mode;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ClassUtils;
import org.springframework.util.unit.DataSize;

import java.util.*;

/**
 * @author liaozan
 * @since 2021/12/18
 */
public class DefaultPropertiesEnvironmentPostProcessor extends EnvironmentPostProcessorAdapter implements Ordered {

    private static final String SPRING_PROFILE_ACTIVE = "spring.profiles.active";
    private static final String DUBBO_REGISTER_KEY = "dubbo.registry.register";

    public DefaultPropertiesEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        super(logFactory, bootstrapContext);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> defaultProperties = new HashMap<>();
        // management
        defaultProperties.put("management.endpoints.web.exposure.include", "*");
        defaultProperties.put("management.endpoints.enabled-by-default", true);
        defaultProperties.put("management.endpoint.health.show-details", Show.ALWAYS);
        defaultProperties.put("management.endpoint.health.show-components", Show.ALWAYS);
        defaultProperties.put("management.info.git.mode", Mode.FULL);
        defaultProperties.put("management.server.port", PortUtils.findAvailablePort(1024));
        // servlet
        defaultProperties.put("spring.servlet.multipart.max-file-size", DataSize.ofBytes(-1));
        defaultProperties.put("spring.servlet.multipart.max-request-size", DataSize.ofBytes(-1));
        // mvc
        defaultProperties.put("spring.mvc.throw-exception-if-no-handler-found", true);
        // datetime
        defaultProperties.put("spring.mvc.format.date", DateTimeFormatters.DATE_PATTERN);
        defaultProperties.put("spring.mvc.format.time", DateTimeFormatters.TIME_PATTERN);
        defaultProperties.put("spring.mvc.format.date-time", DateTimeFormatters.DATE_TIME_PATTERN);
        defaultProperties.put("spring.jackson.date-format", DateTimeFormatters.DATE_TIME_PATTERN);
        defaultProperties.put("spring.jackson.time-zone", TimeZone.getDefault());
        // others
        defaultProperties.put("spring.web.resources.add-mappings", false);
        defaultProperties.put("spring.main.allow-circular-references", true);
        defaultProperties.put("spring.main.banner-mode", Banner.Mode.OFF);
        defaultProperties.put("server.shutdown", Shutdown.GRACEFUL);
        // dubbo
        configureDubboRegistrationIfPresent(environment, defaultProperties);
        // active profile
        configureActiveProfileIfPresent(environment, defaultProperties);
        environment.setDefaultProfiles(EnvUtils.DEVELOPMENT);
        DefaultPropertiesPropertySource.addOrMerge(defaultProperties, environment.getPropertySources());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void configureActiveProfileIfPresent(ConfigurableEnvironment environment, Map<String, Object> defaultProperties) {
        if (ArrayUtil.isEmpty(environment.getActiveProfiles())) {
            environment.setActiveProfiles(EnvUtils.DEVELOPMENT);
            defaultProperties.put(SPRING_PROFILE_ACTIVE, EnvUtils.DEVELOPMENT);
            getLog().info(StrFormatter.format("{} is unset, set to {} by default", SPRING_PROFILE_ACTIVE, EnvUtils.DEVELOPMENT));
        }
    }

    private void configureDubboRegistrationIfPresent(ConfigurableEnvironment environment, Map<String, Object> defaultProperties) {
        if (!dubboInClassPath()) {
            return;
        }
        if (EnvUtils.runningOnCloudPlatform(environment)) {
            return;
        }
        if (!environment.containsProperty(DUBBO_REGISTER_KEY)) {
            getLog().info(StrFormatter.format("Not running on CloudPlatform, {} is set to false by default", DUBBO_REGISTER_KEY));
            getLog().info(StrFormatter.format("If you want force to register with Dubbo Registry, set {} = true", DUBBO_REGISTER_KEY));
            defaultProperties.put(DUBBO_REGISTER_KEY, false);
        }
    }

    private boolean dubboInClassPath() {
        return ClassUtils.isPresent("org.apache.dubbo.config.bootstrap.DubboBootstrap", getClass().getClassLoader());
    }

}