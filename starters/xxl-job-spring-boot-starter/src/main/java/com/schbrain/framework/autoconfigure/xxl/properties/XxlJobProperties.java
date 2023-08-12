package com.schbrain.framework.autoconfigure.xxl.properties;

import com.schbrain.common.util.HostInfoHolder;
import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liaozan
 * @since 2022/1/8
 */
@Data
@ConfigurationProperties(prefix = "schbrain.xxl")
public class XxlJobProperties implements ConfigurableProperties {

    private String adminAddresses;

    private String ip = HostInfoHolder.getHostInfo().getIpAddress();

    private int port = -1;

    private String accessToken;

    private int logRetentionDays = 7;

    private boolean register = false;

    @Override
    public String getNamespace() {
        return "xxl-job-common";
    }

}
