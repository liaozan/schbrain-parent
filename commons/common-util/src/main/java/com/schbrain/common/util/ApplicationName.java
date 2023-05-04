package com.schbrain.common.util;

import cn.hutool.extra.spring.SpringUtil;
import com.schbrain.common.exception.BaseException;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;

import java.util.Optional;

/**
 * @author liaozan
 * @since 2021/12/31
 */
public class ApplicationName {

    public static String get() {
        return Optional.ofNullable(SpringUtil.getApplicationContext())
                .map(EnvironmentCapable::getEnvironment)
                .map(ApplicationName::get)
                .orElseThrow(() -> new BaseException("Could not get application name"));
    }

    public static String get(Environment environment) {
        return environment.getRequiredProperty("spring.application.name");
    }

}