package com.schbrain.framework.autoconfigure.xxl;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.thread.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author liaozan
 * @since 2022/4/18
 */
@Slf4j
public class SchbrainXxlJobExecutor extends XxlJobSpringExecutor implements InitializingBean, DisposableBean {

    private Field executorRegistryThreadStopField;

    private Field jobLogFileCleanThreadStopField;

    private Field triggerCallbackThreadStopField;

    private volatile boolean started = false;

    public SchbrainXxlJobExecutor() {
        try {
            this.executorRegistryThreadStopField = ExecutorRegistryThread.class.getDeclaredField("toStop");
            ReflectionUtils.makeAccessible(executorRegistryThreadStopField);
        } catch (Exception e) {
            this.executorRegistryThreadStopField = null;
        }
        try {
            this.triggerCallbackThreadStopField = TriggerCallbackThread.class.getDeclaredField("toStop");
            ReflectionUtils.makeAccessible(triggerCallbackThreadStopField);
        } catch (Exception e) {
            this.triggerCallbackThreadStopField = null;
        }
        try {
            this.jobLogFileCleanThreadStopField = JobLogFileCleanThread.class.getDeclaredField("toStop");
            ReflectionUtils.makeAccessible(jobLogFileCleanThreadStopField);
        } catch (Exception e) {
            this.jobLogFileCleanThreadStopField = null;
        }
    }

    @Override
    public void start() throws Exception {
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (started) {
            return;
        }
        super.start();
        started = true;
        log.info("Xxl-job started");
    }

    @Override
    public void destroy() {
        if (!started) {
            return;
        }
        super.destroy();
        resetThreadsStatus();
        started = false;
        log.info("Xxl-job destroyed");
    }

    /**
     * Reset xxl-job related thread to make it support restartable
     */
    private void resetThreadsStatus() {
        ReflectionUtils.setField(executorRegistryThreadStopField, ExecutorRegistryThread.getInstance(), false);
        ReflectionUtils.setField(triggerCallbackThreadStopField, TriggerCallbackThread.getInstance(), false);
        ReflectionUtils.setField(jobLogFileCleanThreadStopField, JobLogFileCleanThread.getInstance(), false);
    }

}