package com.schbrain.common.util.support.task;

import lombok.extern.slf4j.Slf4j;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * @author liaozan
 * @since 2022/1/11
 */
public class UnCaughtExceptionHandleThreadFactory implements ThreadFactory {

    private final ThreadFactory delegate;

    public UnCaughtExceptionHandleThreadFactory(ThreadFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = delegate.newThread(runnable);
        thread.setUncaughtExceptionHandler(new LoggingUnCaughtExceptionHandler());
        return thread;
    }

    @Slf4j
    public static class LoggingUnCaughtExceptionHandler implements UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            log.warn("uncaughtException on {}", thread.getName(), throwable);
            thread.interrupt();
        }

    }

}