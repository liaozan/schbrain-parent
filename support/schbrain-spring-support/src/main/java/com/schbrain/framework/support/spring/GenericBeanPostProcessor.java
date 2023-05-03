package com.schbrain.framework.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.context.*;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

/**
 * @author liaozan
 * @since 2021/11/22
 */
@SuppressWarnings({"unchecked", "unused"})
public abstract class GenericBeanPostProcessor<T> implements SmartInstantiationAwareBeanPostProcessor, ApplicationContextAware {

    private final ResolvableType beanType;

    protected ConfigurableApplicationContext applicationContext;

    protected ConfigurableListableBeanFactory beanFactory;

    protected ConfigurableEnvironment environment;

    public GenericBeanPostProcessor() {
        this.beanType = ResolvableType.forInstance(this).getSuperType().getGeneric(0);
    }

    @Override
    public final Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanType.isAssignableFrom(beanClass)) {
            return doPostProcessBeforeInstantiation((Class<T>) beanClass);
        }
        return null;
    }

    @Override
    public final boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (beanType.isInstance(bean)) {
            return doPostProcessAfterInstantiation((T) bean);
        }
        return true;
    }

    @Override
    public final Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanType.isInstance(bean)) {
            return doPostProcessBeforeInitialization((T) bean, beanName);
        }
        return bean;
    }

    @Override
    public final Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanType.isInstance(bean)) {
            return doPostProcessAfterInitialization((T) bean, beanName);
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Assert.isInstanceOf(ConfigurableApplicationContext.class, context, "Require ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext) context;
        this.beanFactory = this.applicationContext.getBeanFactory();
        this.environment = this.applicationContext.getEnvironment();
    }

    // region Instantiation
    protected T doPostProcessBeforeInstantiation(Class<T> beanClass) {
        return null;
    }

    protected boolean doPostProcessAfterInstantiation(T bean) {
        processAfterInstantiation(bean);
        return true;
    }

    protected void processAfterInstantiation(T bean) throws BeansException {

    }
    // endregion

    // region Initialization
    protected T doPostProcessBeforeInitialization(T bean, String beanName) throws BeansException {
        processBeforeInitialization(bean, beanName);
        return bean;
    }

    protected void processBeforeInitialization(T bean, String beanName) throws BeansException {

    }

    protected T doPostProcessAfterInitialization(T bean, String beanName) throws BeansException {
        processAfterInitialization(bean, beanName);
        return bean;
    }

    protected void processAfterInitialization(T bean, String beanName) throws BeansException {

    }
    // endregion

}