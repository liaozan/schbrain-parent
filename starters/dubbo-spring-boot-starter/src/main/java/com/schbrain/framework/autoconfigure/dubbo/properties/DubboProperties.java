package com.schbrain.framework.autoconfigure.dubbo.properties;

import com.schbrain.common.util.support.ConfigurableProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

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

    @Override
    public String getDefaultNamespace() {
        return "dubbo-common";
    }

}