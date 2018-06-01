package com.daojia.utils;

import com.daojia.suyun.supplierJob.inits.Init;
import djedis.dal.Djedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * 基于Redis实现的分布式锁
 * <p>使用方式</p>
 * @author linhaotian
 * @date 2018/5/15
 */
public final class RedisLock {
    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private RedisLock(){
    }

    /**
     * redis客户端
     */
    private static final Djedis djedis = Init.getDjedis();
    /**
     * redis加锁成功
     */
    private static final String LOCK_SUCCESS = "OK";
    /**
     * djedis.eval正确返回值
     */
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 加锁
     *
     * @param key        锁的key
     * @param value      锁的值
     * @param expireTime 锁的过期时间(单位:秒)
     * @return 加锁结果：成功/失败
     */
    public static boolean lock(String key,String value,int expireTime) {
        try {
            // 加锁成功
            if (LOCK_SUCCESS.equals(djedis.set(key, value, "NX", "EX", expireTime))) {
                logger.info("RedisLock lock success. key:{}; value:{}; expireTime:{}", key, value, expireTime);
                return true;
            }
        } catch (Exception e) {
            logger.error("RedisLock lock error. key:{}; expireMillis:{}", key, expireTime, e);
        }
        logger.warn("RedisLock lock fail. key:{}; expireMillis:{}", key, expireTime);
        return false;
    }

    /**
     * 原子操作释放锁
     *
     * @param key   锁的key
     * @param value 锁的值
     * @return 锁的释放结果
     */
    public static boolean unLock(String key, String value) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = djedis.eval(script, Collections.singletonList(key), Collections.singletonList(value));
            logger.info("RedisLock unlock. key:{}", key);
            if (RELEASE_SUCCESS.equals(result)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("RedisLock unlock error. key:{}", key, e);
        }
        logger.info("释放锁失败,key{}，value：{}", key, value);
        return false;
    }
}
