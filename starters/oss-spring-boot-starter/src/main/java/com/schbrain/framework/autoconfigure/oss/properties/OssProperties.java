package com.schbrain.framework.autoconfigure.oss.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author liaozan
 * @since 2021/12/3
 */
@Data
@ConfigurationProperties(prefix = "schbrain.oss")
public class OssProperties implements ConfigurableProperties {

    private String accessKeyId;

    private String secretAccessKey;

    private String endpoint;

    private String bucketName;

    private String directory;

    private String domain;

    @NestedConfigurationProperty
    private StsProperties sts;

    public boolean isInValid() {
        return accessKeyId == null || secretAccessKey == null;
    }

    @Override
    public String getNamespace() {
        return "oss-common";
    }

    @Data
    public static class StsProperties {

        private String endpoint;

        private String roleArn;

        private String roleSessionName;

        private Long durationSeconds = 900L;

    }

}
