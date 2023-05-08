package com.schbrain.framework.autoconfigure.dubbo.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.dubbo.config.*;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * used to fetch dubbo.* config from remote
 *
 * @author liaozan
 * @since 2021/12/6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "dubbo")
public class DubboProperties extends ConfigurableProperties {

    @NestedConfigurationProperty
    private Scan scan = new Scan();

    @NestedConfigurationProperty
    private ApplicationConfig application = new ApplicationConfig();

    @NestedConfigurationProperty
    private ModuleConfig module = new ModuleConfig();

    @NestedConfigurationProperty
    private RegistryConfig registry = new RegistryConfig();

    @NestedConfigurationProperty
    private ProtocolConfig protocol = new ProtocolConfig();

    @NestedConfigurationProperty
    private MonitorConfig monitor = new MonitorConfig();

    @NestedConfigurationProperty
    private ProviderConfig provider = new ProviderConfig();

    @NestedConfigurationProperty
    private ConsumerConfig consumer = new ConsumerConfig();

    @NestedConfigurationProperty
    private ConfigCenterConfig configCenter = new ConfigCenterConfig();

    @NestedConfigurationProperty
    private MetadataReportConfig metadataReport = new MetadataReportConfig();

    @NestedConfigurationProperty
    private MetricsConfig metrics = new MetricsConfig();

    @NestedConfigurationProperty
    private TracingConfig tracing = new TracingConfig();

    @Override
    public String getDefaultNamespace() {
        return "dubbo-common";
    }

    static class Scan {

        /**
         * The basePackages to scan , the multiple-value is delimited by comma
         *
         * @see EnableDubbo#scanBasePackages()
         */
        private Set<String> basePackages = new LinkedHashSet<>();

        public Set<String> getBasePackages() {
            return basePackages;
        }

        public void setBasePackages(Set<String> basePackages) {
            this.basePackages = basePackages;
        }

    }

}