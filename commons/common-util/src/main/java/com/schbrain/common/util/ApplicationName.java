package com.schbrain.common.util;

import org.springframework.core.env.Environment;

/**
 * @author liaozan
 * @since 2021/12/31
 */
public class ApplicationName {

    public static String get(Environment environment) {
        return environment.getRequiredProperty("spring.application.name");
    }

}
