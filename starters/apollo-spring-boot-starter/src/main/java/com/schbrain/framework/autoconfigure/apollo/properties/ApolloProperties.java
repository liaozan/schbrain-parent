package com.schbrain.framework.autoconfigure.apollo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import static com.schbrain.framework.autoconfigure.apollo.properties.ApolloProperties.PREFIX;

/**
 * this class MUST NOT load from remote
 *
 * @author liaozan
 * @since 2021/12/6
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class ApolloProperties {

    public static final String PREFIX = "schbrain.apollo";

    private boolean remoteFirst = false;

    public static ApolloProperties get(Environment environment) {
        return Binder.get(environment).bindOrCreate(PREFIX, ApolloProperties.class);
    }

}