package com.schbrain.common.util.support.delay;

import cn.hutool.core.text.StrPool;
import cn.hutool.extra.spring.SpringUtil;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.ApplicationName;
import org.redisson.api.*;
import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author lik
 * @since 2023/4/17
 */
public class DelayedQueueUtils {

    private static final String APPLICATION_NAME = ApplicationName.get();

    private static final ConcurrentHashMap<String, RBlockingQueue<?>> blockingQueueCache = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, RDelayedQueue<?>> delayedQueueCache = new ConcurrentHashMap<>();

    private static RedissonClient CLIENT;

    public static <T> void offerMsg(T message, String queueName, Long delay, TimeUnit timeUnit) {
        batchOfferMsg(List.of(message), queueName, delay, timeUnit);
    }

    public static <T> void batchOfferMsg(List<T> messages, String queueName, Long delay, TimeUnit timeUnit) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        RDelayedQueue<T> delayedQueue = getDelayedQueue(queueName);
        messages.forEach(msg -> delayedQueue.offer(msg, delay, timeUnit));
    }

    public static <T> T takeMsg(String queueName) {
        RBlockingQueue<T> blockingQueue = getBlockingQueue(queueName);
        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            throw new BaseException("redis blocking queue thread has been interrupted", e);
        }
    }

    public static <T> Boolean removeMsg(String queueName, T msg) {
        if (msg == null) {
            return false;
        }
        RDelayedQueue<Object> delayedQueue = getDelayedQueue(queueName);
        return delayedQueue.remove(msg);
    }

    public static void removeQueue(String queueName) {
        String wrappedQueueName = withPrefix(queueName);
        RBlockingQueue<?> blockingQueue = blockingQueueCache.get(wrappedQueueName);
        RDelayedQueue<?> delayedQueue = delayedQueueCache.get(wrappedQueueName);
        if (delayedQueue != null) {
            delayedQueue.destroy();
            delayedQueueCache.remove(wrappedQueueName);
        }
        if (blockingQueue != null) {
            blockingQueue.delete();
            blockingQueueCache.remove(wrappedQueueName);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> RDelayedQueue<T> getDelayedQueue(String queueName) {
        String cacheKey = withPrefix(queueName);
        return (RDelayedQueue<T>) delayedQueueCache.computeIfAbsent(cacheKey, key -> {
            RBlockingQueue<?> blockingQueue = getBlockingQueue(queueName);
            return getClient().getDelayedQueue(blockingQueue);
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> RBlockingQueue<T> getBlockingQueue(String queueName) {
        String cacheKey = withPrefix(queueName);
        return (RBlockingQueue<T>) blockingQueueCache.computeIfAbsent(cacheKey, key -> getClient().getBlockingQueue(cacheKey));
    }

    private static RedissonClient getClient() {
        if (CLIENT == null) {
            try {
                CLIENT = SpringUtil.getBean(RedissonClient.class);
            } catch (BeansException e) {
                throw new BaseException("Could not get RedissonClient, please put it into Spring container", e);
            }
        }
        return CLIENT;
    }

    private static String withPrefix(String queueName) {
        return String.join(StrPool.COLON, APPLICATION_NAME, "delay-queue", queueName);
    }

}