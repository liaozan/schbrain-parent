package com.schbrain.framework.support.spring.env;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ArrayUtil;
import com.schbrain.common.util.EnvUtils;
import com.schbrain.common.util.PortUtils;
import com.schbrain.framework.support.spring.LoggerAwareEnvironmentPostProcessor;
import org.springframework.boot.Banner;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.health.HealthProperties.Show;
import org.springframework.boot.actuate.info.InfoPropertiesInfoContributor.Mode;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.unit.DataSize;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author liaozan
 * @since 2021/12/18
 */
public class DefaultPropertiesEnvironmentPostProcessor extends LoggerAwareEnvironmentPostProcessor implements Ordered {

    /**
     * set default properties after configData loaded
     */
    public static final Integer DEFAULT_ORDER = ConfigDataEnvironmentPostProcessor.ORDER + 1;

    private static final String SPRING_PROFILE_ACTIVE = "spring.profiles.active";

    public DefaultPropertiesEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        super(logFactory, bootstrapContext);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> defaultProperties = new HashMap<>();
        // management
        defaultProperties.put("management.trace.http.enabled", false);
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
        defaultProperties.put("spring.mvc.format.date", DatePattern.NORM_DATE_PATTERN);
        defaultProperties.put("spring.mvc.format.time", DatePattern.NORM_TIME_PATTERN);
        defaultProperties.put("spring.mvc.format.date-time", DatePattern.NORM_DATETIME_PATTERN);
        defaultProperties.put("spring.jackson.date-format", DatePattern.NORM_DATETIME_PATTERN);
        defaultProperties.put("spring.jackson.time-zone", TimeZone.getDefault());
        // others
        defaultProperties.put("spring.mandatory-file-encoding", StandardCharsets.UTF_8.name());
        defaultProperties.put("spring.web.resources.add-mappings", false);
        defaultProperties.put("spring.main.allow-circular-references", true);
        defaultProperties.put("spring.main.banner-mode", Banner.Mode.OFF);
        defaultProperties.put("server.shutdown", Shutdown.GRACEFUL);
        // active profile
        configureActiveProfileIfPresent(environment, defaultProperties);
        environment.setDefaultProfiles(EnvUtils.DEVELOPMENT);
        DefaultPropertiesPropertySource.addOrMerge(defaultProperties, environment.getPropertySources());
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    private void configureActiveProfileIfPresent(ConfigurableEnvironment environment, Map<String, Object> defaultProperties) {
        if (ArrayUtil.isEmpty(environment.getActiveProfiles())) {
            environment.setActiveProfiles(EnvUtils.DEVELOPMENT);
            defaultProperties.put(SPRING_PROFILE_ACTIVE, EnvUtils.DEVELOPMENT);
            log.info(StrFormatter.format("{} is unset, set to {} by default", SPRING_PROFILE_ACTIVE, EnvUtils.DEVELOPMENT));
        }
    }

}
