package com.daojia.utils;

import com.google.common.base.Predicates;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Created by 13520 on 2018/5/8.
 */
public class RetryUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtilTest.class);
    private Callable<Integer> task;
    private Callable<Integer> task2;
    private Callable<Boolean> task3;

    @Before
    public void before() {
        // here shows thress kinds of test case
        task = () -> {
            int a = 1 / 0;
            return 2;
        };
        task2 = () -> {
            Thread.sleep(2000L);
            return 2;
        };
        task3 = () -> {
            return false;
        };
    }

    @Test(expected=Exception.class)
    public void test1() {
        //异常重试
        Integer result = Optional.of(RetryUtil.retry(task, 30L, 1000L, TimeUnit.MILLISECONDS, 3)).get();
        logger.info("result: {}", result);
    }
    @Test
    public void test2(){
        //超时重试
        Integer result = Optional.of(RetryUtil.retry(task2, 30L, 3000L, TimeUnit.MILLISECONDS, 3)).get();
        logger.info("result: {}", result);
    }
    @Test
    public void test3(){
        Boolean result = Optional.of(RetryUtil.retry(task3, Predicates.equalTo(false),30L, 1000L, TimeUnit.MILLISECONDS, 3)).get();

        logger.info("result: {}", result);
    }
}
