package com.schbrain.common.web.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

/**
 * @author liaozan
 * @since 2022/8/29
 */
@Data
@ConfigurationProperties(prefix = "schbrain.web")
public class WebProperties implements ConfigurableProperties {

    /**
     * whether to enable the request logging
     */
    private boolean enableRequestLogging = true;

    /**
     * whether to enable the response wrap
     */
    private boolean wrapResponse = true;

    /**
     * whether to enable the global exception handing
     */
    private boolean enableGlobalExceptionHandler = true;

    /**
     * encoding for request/response
     */
    private String encoding = StandardCharsets.UTF_8.name();

    /**
     * authenticationVariableName for login auth
     */
    private String authenticationVariableName = "token";

    @Override
    public String getNamespace() {
        return "web-common";
    }

}