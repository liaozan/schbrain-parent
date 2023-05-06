package com.schbrain.framework.autoconfigure.apollo.event;

import cn.hutool.core.thread.GlobalThreadPool;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.AbstractApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.scheduling.support.TaskUtils;

import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * expose {@link AbstractApplicationEventMulticaster#getApplicationListeners(ApplicationEvent, ResolvableType)}
 *
 * @author liaozan
 * @since 2023-05-06
 */
public class ConcurrentEventMulticaster extends SimpleApplicationEventMulticaster {

    public ConcurrentEventMulticaster() {
        this(GlobalThreadPool.getExecutor());
    }

    public ConcurrentEventMulticaster(Executor executor) {
        setTaskExecutor(executor);
        setErrorHandler(TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER);
    }

    @Override
    public Collection<ApplicationListener<?>> getApplicationListeners() {
        return super.getApplicationListeners();
    }

}