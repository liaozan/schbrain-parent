package com.schbrain.framework.support.spring;

import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.*;
import org.springframework.context.event.ApplicationContextEvent;

import static org.springframework.util.ObjectUtils.nullSafeEquals;

/**
 * 使用此类可以避免事件被触发两次导致意外
 * <p>
 * 当类路径下存在 actuator,并且指定了不同端口的时候,实现了{@link ApplicationContextEvent}的事件会触发两次
 *
 * @author liaozan
 * @see org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration.DifferentManagementContextConfiguration#onApplicationEvent(WebServerInitializedEvent)
 * @since 2023/7/9
 */
@SuppressWarnings("JavadocReference")
public abstract class OnceApplicationContextEventListener<E extends ApplicationEvent> implements ApplicationListener<E>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public OnceApplicationContextEventListener() {
        this(null);
    }

    public OnceApplicationContextEventListener(ApplicationContext applicationContext) {
        setApplicationContext(applicationContext);
    }

    @Override
    public void onApplicationEvent(E event) {
        if (nullSafeEquals(getApplicationContext(), event.getSource())) {
            onEvent(event);
        }
    }

    protected ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicationContext must be not null");
        }
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected abstract void onEvent(E event);

}
