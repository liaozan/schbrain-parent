package com.schbrain.framework.support.spring.admin;

import de.codecentric.boot.admin.client.config.InstanceProperties;
import de.codecentric.boot.admin.client.config.SpringBootAdminClientAutoConfiguration;
import de.codecentric.boot.admin.client.registration.ApplicationFactory;
import de.codecentric.boot.admin.client.registration.metadata.MetadataContributor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContext;

/**
 * @author liaozan
 * @since 2023/10/26
 */
@AutoConfiguration(before = SpringBootAdminClientAutoConfiguration.class)
@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
public class SpringBootAdminFeatureAutoConfiguration {

    @Bean
    public ApplicationFactory applicationFactory(InstanceProperties instance, ManagementServerProperties management,
                                                 ServerProperties server, ServletContext servletContext, PathMappedEndpoints pathMappedEndpoints,
                                                 WebEndpointProperties webEndpoint, MetadataContributor metadataContributor,
                                                 DispatcherServletPath dispatcherServletPath) {
        return new AdminApplicationFactory(instance, management, server, servletContext, pathMappedEndpoints, webEndpoint, metadataContributor, dispatcherServletPath);
    }

}
