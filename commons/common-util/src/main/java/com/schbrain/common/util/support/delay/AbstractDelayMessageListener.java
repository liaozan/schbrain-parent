package com.schbrain.common.util.support.delay;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lik
 * @since 2023/4/17
 */
@Slf4j
public abstract class AbstractDelayMessageListener<Message> implements Runnable, InitializingBean, DisposableBean {

    private final String threadNamePrefix;

    private final String queueName;

    private final String listenerName;

    private final AtomicBoolean started = new AtomicBoolean(false);

    private ExecutorService executor;

    public AbstractDelayMessageListener(String queueName) {
        this(null, queueName);
    }

    public AbstractDelayMessageListener(String threadNamePrefix, String queueName) {
        this.listenerName = getClass().getSimpleName();
        this.threadNamePrefix = threadNamePrefix == null ? listenerName + "#delay-message-" : threadNamePrefix;
        this.queueName = queueName;
    }

    @Override
    public void run() {
        try {
            while (started.get()) {
                Message message = DelayedQueueUtils.takeMsg(queueName);
                if (log.isDebugEnabled()) {
                    log.debug("{} receive message ：{}", listenerName, message);
                }
                onMessage(message);
            }
        } catch (Exception e) {
            log.error("{} occur error: {}", listenerName, e.getMessage(), e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        executor = ThreadUtil.newFixedExecutor(1, threadNamePrefix, true);
        started.set(true);
        executor.execute(this);
        log.info("{} start listen...", listenerName);
    }

    @Override
    public void destroy() {
        started.set(false);
        executor.shutdown();
        log.info("{} stop listen... ", listenerName);
    }

    /**
     * for subClass
     */
    protected abstract void onMessage(Message message);

}