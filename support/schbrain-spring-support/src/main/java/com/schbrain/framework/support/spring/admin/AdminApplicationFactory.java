package com.schbrain.framework.support.spring.admin;

import com.schbrain.common.util.IpAddressHolder;
import de.codecentric.boot.admin.client.config.InstanceProperties;
import de.codecentric.boot.admin.client.registration.ServletApplicationFactory;
import de.codecentric.boot.admin.client.registration.metadata.MetadataContributor;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;

import javax.servlet.ServletContext;

/**
 * @author liaozan
 * @since 2023/10/26
 */
public class AdminApplicationFactory extends ServletApplicationFactory {

    public AdminApplicationFactory(InstanceProperties instance, ManagementServerProperties management,
                                   ServerProperties server, ServletContext servletContext, PathMappedEndpoints pathMappedEndpoints,
                                   WebEndpointProperties webEndpoint, MetadataContributor metadataContributor,
                                   DispatcherServletPath dispatcherServletPath) {
        super(instance, management, server, servletContext, pathMappedEndpoints, webEndpoint, metadataContributor, dispatcherServletPath);
    }

    @Override
    protected String getServiceHost() {
        return IpAddressHolder.getIpAddress();
    }

    @Override
    protected String getManagementHost() {
        return IpAddressHolder.getIpAddress();
    }

}
