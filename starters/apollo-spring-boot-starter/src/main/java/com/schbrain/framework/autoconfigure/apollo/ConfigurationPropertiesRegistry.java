package com.schbrain.framework.autoconfigure.apollo;

import cn.hutool.core.text.StrPool;
import com.ctrip.framework.apollo.spring.property.SpringValue;
import com.ctrip.framework.apollo.spring.property.SpringValueRegistry;
import com.ctrip.framework.apollo.spring.util.SpringInjector;
import com.google.common.base.CaseFormat;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import static org.springframework.beans.factory.config.PlaceholderConfigurerSupport.*;

/**
 * @author liaozan
 * @since 2022/9/19
 */
public class ConfigurationPropertiesRegistry implements SmartInitializingSingleton, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private AutowireCapableBeanFactory beanFactory;

    private SpringValueRegistry springValueRegistry;

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, ConfigurationPropertiesBean> beanMap = ConfigurationPropertiesBean.getAll(applicationContext);
        if (MapUtils.isEmpty(beanMap)) {
            return;
        }
        beanMap.forEach((beanName, propertiesBean) -> {
            String prefix = propertiesBean.getAnnotation().prefix();
            Object instance = propertiesBean.getInstance();
            ReflectionUtils.doWithFields(instance.getClass(), field -> register(beanName, prefix, instance, field), this::isNotFinalField);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
        this.springValueRegistry = SpringInjector.getInstance(SpringValueRegistry.class);
    }

    private void register(String beanName, String prefix, Object instance, Field field) {
        String propertyName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.getName());
        String key = prefix + StrPool.DOT + propertyName;
        String placeholder = toPlaceHolder(key);
        SpringValue springValue = new SpringValue(key, placeholder, instance, beanName, field, false);
        springValueRegistry.register(beanFactory, key, springValue);
    }

    private String toPlaceHolder(String key) {
        return DEFAULT_PLACEHOLDER_PREFIX + key + DEFAULT_VALUE_SEPARATOR + DEFAULT_PLACEHOLDER_SUFFIX;
    }

    private boolean isNotFinalField(Field field) {
        return !Modifier.isFinal(field.getModifiers());
    }

}