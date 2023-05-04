package com.schbrain.common.util.support.lock;

import cn.hutool.core.text.StrPool;
import cn.hutool.extra.spring.SpringUtil;
import com.schbrain.common.exception.BaseException;
import com.schbrain.common.util.ApplicationName;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author liaozan
 * @since 2023-03-15
 */
@Slf4j
public class RedisLockUtils {

    private static final String APPLICATION_NAME = ApplicationName.get();

    private static RedissonClient CLIENT;

    public static void executeWithLock(String lockName, Runnable action) {
        executeWithLock(lockName, Duration.ofSeconds(3), action);
    }

    public static void executeWithLock(String lockName, Duration timeout, Runnable action) {
        executeWithLock(lockName, timeout, () -> {
            action.run();
            return null;
        });
    }

    public static <T> T executeWithLock(String lockName, Callable<T> action) {
        return executeWithLock(lockName, Duration.ofSeconds(3), action);
    }

    public static <T> T executeWithLock(String lockName, Duration timeout, Callable<T> action) {
        RLock lock = getClient().getLock(withPrefix(lockName));
        boolean locked;
        try {
            locked = lock.tryLock(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new BaseException("Lock thread has been interrupted", e);
        }
        if (!locked) {
            throw new BaseException(String.format("Lock cannot be acquired within %d seconds", timeout.toSeconds()));
        }

        try {
            return action.call();
        } catch (Exception e) {
            throw new BaseException(e.getMessage(), e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
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

    private static String withPrefix(String lockName) {
        return String.join(StrPool.COLON, APPLICATION_NAME, "lock", lockName);
    }

}