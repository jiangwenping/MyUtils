package com.daojia.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * 简化Redis分布式锁的使用，不用关心锁的释放
 * <p> 使用方式： </p>
 * ResultDTO result = new SimpleRedisLock().execute(lockKey, LOCK_TIMEOUT, () -> {
 *    // 这里的代码是锁上的
 * });
 *
 * @Author linhaotian
 * @date 2018/5/15
 */

public class SimpleRedisLock<T> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleRedisLock.class);

    /**
     * 加锁并执行锁代码
     *
     * @param key        锁住的key
     * @param expireTime 锁的超时时间
     * @param lockCode   加锁的代码
     * @param retryStrategy 重试策略 (传null表示不需要重试)
     * @return 加锁代码执行结果
     */
    public boolean execute(String key, int expireTime, LockCode lockCode, RetryStrategy retryStrategy) {
        // 用来防止持有过期锁的客户端误删现有锁的情况
        String uuid = UUID.randomUUID().toString();
        for (int retryCount = 1;retryCount<Integer.MAX_VALUE; retryCount++) {
            // 加锁成功
            if (RedisLock.lock(key, uuid, expireTime)) {
                try {
                    // 执行加锁代码
                    return lockCode.run();
                } catch (Exception e) {
                    logger.error("execute lockCode error. key:{},exceprion:{}", key, e);
                    logger.error("execute lockCode error", e);
                } finally {
                    // 释放锁
                    RedisLock.unLock(key, uuid);
                }
            }
        }
        return false;
    }

    public boolean execute(String key, int expireTime, LockCode lockCode) {
        // 某些情况，不允许并发的存在，一旦并发就返回失败
        return execute(key, expireTime, lockCode, null);
    }

    /**
     * 加锁代码
     */
    public interface LockCode<T> {
       boolean run();
    }
}
