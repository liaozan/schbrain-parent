package com.schbrain.framework.autoconfigure.xxl.condition;

import com.schbrain.common.util.EnvUtils;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author liaozan
 * @since 2022/4/20
 */
public class XxlJobShouldAvailableCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        if (EnvUtils.runningOnCloudPlatform(environment)) {
            return ConditionOutcome.match("runningOnCloudPlatform");
        }
        Boolean registerWithServer = environment.getProperty("schbrain.xxl.register", boolean.class, false);
        if (Boolean.TRUE.equals(registerWithServer)) {
            return ConditionOutcome.match("schbrain.xxl.register is true");
        }
        return ConditionOutcome.noMatch("It is neither running on the cloud platform nor enabled");
    }

}