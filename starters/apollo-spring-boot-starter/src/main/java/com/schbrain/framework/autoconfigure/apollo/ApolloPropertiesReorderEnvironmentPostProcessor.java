package com.schbrain.framework.autoconfigure.apollo;

import com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer;
import com.schbrain.framework.autoconfigure.apollo.util.PropertySourceOrderUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author liaozan
 * @since 2022/4/19
 */
@Order(ApolloApplicationContextInitializer.DEFAULT_ORDER + 1)
public class ApolloPropertiesReorderEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        PropertySourceOrderUtils.adjustPropertySourceOrder(environment);
    }

}