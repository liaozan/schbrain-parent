package com.schbrain.framework.support.spring.env;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ArrayUtil;
import com.schbrain.common.util.*;
import com.schbrain.framework.support.spring.LoggerAwareEnvironmentPostProcessor;
import org.springframework.boot.*;
import org.springframework.boot.actuate.autoconfigure.health.HealthProperties.Show;
import org.springframework.boot.actuate.info.InfoPropertiesInfoContributor.Mode;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.boot.web.server.Shutdown;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.unit.DataSize;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author liaozan
 * @since 2021/12/18
 */
public class DefaultPropertiesEnvironmentPostProcessor extends LoggerAwareEnvironmentPostProcessor implements Ordered {

    /**
     * set default properties after configData loaded
     */
    public static final int DEFAULT_ORDER = ConfigDataEnvironmentPostProcessor.ORDER + 1;

    private static final String SPRING_PROFILE_ACTIVE = "spring.profiles.active";

    public DefaultPropertiesEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        super(logFactory, bootstrapContext);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> defaultProperties = new LinkedHashMap<>();
        // active profile
        configureActiveProfileIfPresent(environment, defaultProperties);
        environment.setDefaultProfiles(EnvUtils.DEVELOPMENT);
        // management
        defaultProperties.put("management.endpoints.web.exposure.include", "*");
        defaultProperties.put("management.endpoints.enabled-by-default", true);
        defaultProperties.put("management.endpoint.health.show-details", Show.ALWAYS.name());
        defaultProperties.put("management.endpoint.health.show-components", Show.ALWAYS.name());
        defaultProperties.put("management.info.git.mode", Mode.FULL.name());
        defaultProperties.put("management.metrics.tags.application", ApplicationName.get(environment));
        defaultProperties.put("management.server.port", PortUtils.findAvailablePort(1024));
        defaultProperties.put("management.trace.http.enabled", false);
        // servlet
        defaultProperties.put("spring.servlet.multipart.max-file-size", DataSize.ofBytes(-1).toString());
        defaultProperties.put("spring.servlet.multipart.max-request-size", DataSize.ofBytes(-1).toString());
        // mvc
        defaultProperties.put("spring.mvc.throw-exception-if-no-handler-found", true);
        // datetime
        defaultProperties.put("spring.mvc.format.date", DatePattern.NORM_DATE_PATTERN);
        defaultProperties.put("spring.mvc.format.time", DatePattern.NORM_TIME_PATTERN);
        defaultProperties.put("spring.mvc.format.date-time", DatePattern.NORM_DATETIME_PATTERN);
        defaultProperties.put("spring.jackson.date-format", DatePattern.NORM_DATETIME_PATTERN);
        defaultProperties.put("spring.jackson.time-zone", TimeZone.getDefault().getID());
        // kafka
        defaultProperties.put("spring.kafka.consumer.group-id", ApplicationName.get(environment));
        // others
        defaultProperties.put("spring.mandatory-file-encoding", StandardCharsets.UTF_8.name());
        defaultProperties.put("spring.web.resources.add-mappings", false);
        defaultProperties.put("spring.main.allow-circular-references", true);
        defaultProperties.put("spring.main.banner-mode", Banner.Mode.OFF.name());
        defaultProperties.put("server.shutdown", Shutdown.GRACEFUL.name());
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
