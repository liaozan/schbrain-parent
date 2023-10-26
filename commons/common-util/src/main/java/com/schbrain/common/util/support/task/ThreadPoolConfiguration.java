package com.schbrain.common.util.support.task;

import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liaozan
 * @since 2021/11/22
 */
@Configuration(proxyBeanMethods = false)
public class ThreadPoolConfiguration {

    @Bean
    public TaskExecutorCustomizer exceptionHandlerAwareTaskExecutorCustomizer() {
        return taskExecutor -> {
            taskExecutor.setTaskDecorator(new MdcContextPropagationTaskDecorator());
            taskExecutor.setThreadFactory(new UnCaughtExceptionHandlerThreadFactory(taskExecutor));
        };
    }

    @Bean
    public TaskSchedulerCustomizer exceptionHandlerAwareTaskSchedulerCustomizer() {
        return taskScheduler -> taskScheduler.setThreadFactory(new UnCaughtExceptionHandlerThreadFactory(taskScheduler));
    }

}
