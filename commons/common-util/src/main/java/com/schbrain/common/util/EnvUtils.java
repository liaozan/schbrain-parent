package com.schbrain.common.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.core.env.Environment;

/**
 * 注意！！ 此类获取 profile 的方法不能用在{@link BeanFactoryPostProcessor} 之前
 *
 * @author liaozan
 * @see cn.hutool.extra.spring.SpringUtil
 * @since 2021/12/13
 */
public class EnvUtils {

    public static final String DEVELOPMENT = "dev";
    public static final String TESTING = "test";
    public static final String PRODUCTION = "prod";

    public static boolean isDevelopment() {
        return isDevelopment(getProfile());
    }

    public static boolean isDevelopment(String profile) {
        return DEVELOPMENT.equals(profile);
    }

    public static boolean isTesting() {
        return isTesting(getProfile());
    }

    public static boolean isTesting(String profile) {
        return TESTING.equals(profile);
    }

    public static boolean isProduction() {
        return isProduction(getProfile());
    }

    public static boolean isProduction(String profile) {
        return PRODUCTION.equals(profile);
    }

    public static String getProfile() {
        Environment environment = SpringUtil.getBean(Environment.class);
        return getProfile(environment);
    }

    public static String getProfile(Environment environment) {
        String[] profiles = environment.getActiveProfiles();
        if (ArrayUtil.isEmpty(profiles)) {
            profiles = environment.getDefaultProfiles();
        }
        return profiles[0];
    }

    public static boolean runningOnCloudPlatform(Environment environment) {
        CloudPlatform cloudPlatform = CloudPlatform.getActive(environment);
        return cloudPlatform != null && cloudPlatform != CloudPlatform.NONE;
    }

}