package com.daojia.utils;

import com.github.rholder.retry.*;
import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by 13520 on 2018/5/8.
 */
public class RetryUtil {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);
    /**
     * @param task             要重试执行得任务
     * @param fixedWaitTime    本次重试与上次重试之间的固定间隔时长
     * @param maxEachExecuTime 一次重试的最大执行的时间
     * @param timeUnit         时间单位
     * @param attemptNumber    重试次数
     */
    public static <T> T retry(Callable<T> task, long fixedWaitTime, long maxEachExecuTime, TimeUnit timeUnit, int attemptNumber) {
        Retryer<T> retryer = RetryerBuilder
                .<T>newBuilder()
                //抛出runtime异常、checked异常时都会重试，但是抛出error不会重试。
                .retryIfException()
                //重试策略
                .withWaitStrategy(WaitStrategies.fixedWait(fixedWaitTime, timeUnit))
                //尝试次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(attemptNumber))
                //每次重试执行的最大时间限制
                .withAttemptTimeLimiter(AttemptTimeLimiters.<T>fixedTimeLimit(maxEachExecuTime, timeUnit))
                //重试监听器
                .withRetryListener(new RetryListener() {
                                       @Override
                                       public <V> void onRetry(Attempt<V> attempt) {
                                           if (attempt.hasException()) {
                                               logger.error("第【{}】次重试失败", attempt.getAttemptNumber(), attempt.getExceptionCause());
                                           }
                                       }
                                   }
                ).build();
        T t = null;
        try {
            t = retryer.call(task);
        } catch (ExecutionException e) {
            logger.error("", e);
        } catch (RetryException e) {
            logger.error("", e);
        }
        return t;
    }
    /**
     * @param task             要重试执行得任务
     * @param predicate        符合预期结果需要重试
     * @param fixedWaitTime    本次重试与上次重试之间的固定间隔时长
     * @param maxEachExecuTime 一次重试的最大执行的时间
     * @param attemptNumber    重试次数
     */
    public static <T> T retry(Callable<T> task, Predicate<T> predicate, long fixedWaitTime, long maxEachExecuTime, TimeUnit timeUnit, int attemptNumber) {
        Retryer<T> retryer = RetryerBuilder
                .<T>newBuilder()
                //抛出runtime异常、checked异常时都会重试，但是抛出error不会重试。
                .retryIfException()
                //对执行结果的预期。符合预期就重试
                .retryIfResult(predicate)
                //每次重试固定等待fixedWaitTime时间
                .withWaitStrategy(WaitStrategies.fixedWait(fixedWaitTime, timeUnit))
                //尝试次数
                .withStopStrategy(StopStrategies.stopAfterAttempt(attemptNumber))
                //每次重试执行的最大时间限制（在规定的时间内没有返回结果会TimeoutException）
                .withAttemptTimeLimiter(AttemptTimeLimiters.<T>fixedTimeLimit(maxEachExecuTime, timeUnit))
                //重试监听器
                .withRetryListener(new RetryListener() {
                                       @Override
                                       public <V> void onRetry(Attempt<V> attempt) {
                                           if (attempt.hasException()) {
                                               logger.error("第【{}】次重试失败", attempt.getAttemptNumber(), attempt.getExceptionCause());
                                           }
                                       }
                                   }
                ).build();
        T t = null;
        try {
            t = retryer.call(task);
        } catch (ExecutionException e) {
            logger.error("", e);
        } catch (RetryException e) {
            logger.error("", e);
        }
        return t;
    }
}
