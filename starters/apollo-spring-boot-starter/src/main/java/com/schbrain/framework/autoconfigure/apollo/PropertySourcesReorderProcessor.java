package com.schbrain.framework.autoconfigure.apollo;

import com.ctrip.framework.apollo.spring.config.PropertySourcesProcessor;
import com.schbrain.framework.autoconfigure.apollo.util.PropertySourceOrderUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author liaozan
 * @since 2021/12/6
 */
public class PropertySourcesReorderProcessor extends PropertySourcesProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        super.postProcessBeanFactory(beanFactory);
        PropertySourceOrderUtils.adjustPropertySourceOrder(beanFactory.getBean(ConfigurableEnvironment.class));
    }

}