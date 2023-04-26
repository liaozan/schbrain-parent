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
    public TaskExecutorCustomizer mdcSupportTaskExecutorCustomizer() {
        return taskExecutor -> {
            taskExecutor.setTaskDecorator(new MdcContextPropagationTaskDecorator());
            taskExecutor.setThreadFactory(new UnCaughtExceptionHandleThreadFactory(taskExecutor));
        };
    }

    @Bean
    public TaskSchedulerCustomizer mdcSupportTaskSchedulerCustomizer() {
        return taskScheduler -> taskScheduler.setThreadFactory(new UnCaughtExceptionHandleThreadFactory(taskScheduler));
    }

}