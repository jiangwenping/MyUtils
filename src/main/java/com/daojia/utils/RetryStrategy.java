package com.daojia.utils;

/**
 * 重试策略
 * @author linhaotian
 * @date 2018/5/15
 */
public interface RetryStrategy {

    /**
     * 判断是否需要重试
     * @param retryCount
     * @return
     */
    boolean needRetry(int retryCount);

    /**
     * 重试间隔时间
     * @return
     */
    long getRetryInterval();
}
