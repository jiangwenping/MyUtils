package com.daojia.utils;

/**
 * 默认重试策略
 * @author linhaotian
 * @date 2018/5/15
 */
public class DefaultRetryStrategy implements RetryStrategy {

    // 默认重试次数
    private static final int DEFAULT_RETRY_COUNT = 5;
    // 默认重试间隔时间(单位毫秒)
    private static final long DEFAULT_RETRY_INTERVAL = 100;

    /**
     * 最大重试次数
     */
    private int maxRetryCount;
    /**
     * 默认重试间隔时间(单位毫秒)
     */
    private long retryInterval;

    /**
     * @param maxRetryCount 最大重试的次数
     * @param retryInterval 重试间隔时间
     */
    public DefaultRetryStrategy(int maxRetryCount, long retryInterval) {
        this.maxRetryCount = maxRetryCount;
        this.retryInterval = retryInterval;
    }

    public DefaultRetryStrategy() {
        this.maxRetryCount = DEFAULT_RETRY_COUNT;
        this.retryInterval = DEFAULT_RETRY_INTERVAL;
    }

    @Override
    public boolean needRetry(int retryCount) {
        return retryCount <= maxRetryCount;
    }

    @Override
    public long getRetryInterval() {
        return retryInterval;
    }
}
