package com.schbrain.framework.autoconfigure.dubbo.initializer;

import com.schbrain.common.util.properties.OrderedMapPropertySource;
import org.springframework.util.ClassUtils;

/**
 * 暂时以编程的方式配置参数校验。待所有服务都升级到指定版本时,再通过配置开启
 *
 * @author liaozan
 * @since 2023-07-04
 */
public class DubboValidationInitializer {

    private static final String VALIDATION_FILTER_CLASS_NAME = "org.apache.dubbo.validation.filter.ValidationFilter";

    private static final String PROVIDER_VALIDATION_PROPERTY = "dubbo.provider.validation";

    public static void initialize(OrderedMapPropertySource propertySource) {
        if (ClassUtils.isPresent(VALIDATION_FILTER_CLASS_NAME, DubboValidationInitializer.class.getClassLoader())) {
            if (!propertySource.containsProperty(PROVIDER_VALIDATION_PROPERTY)) {
                propertySource.addProperty(PROVIDER_VALIDATION_PROPERTY, Boolean.TRUE.toString());
            }
        }
    }

}