package com.sbank.admin;

import com.sbank.admin.common.exception.AsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    private static ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static String THREAD_NAME  = "ASYNC-THREAD-";

    private static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors(); // 최초에 생성할 Pool Size
    private static int MAX_POOL_SIZE  = Runtime.getRuntime().availableProcessors(); // 최대 Pool Size
    private static int QUEUE_SIZE     = 100; // 유후 스레드 수 만큼만 작업하는 방식 구현이므로 영향을 주지 않음

    @Override
    @Bean(name = "asyncExecutor")
    public Executor getAsyncExecutor() {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

        threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
        threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
        threadPoolTaskExecutor.setQueueCapacity(QUEUE_SIZE);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(false);
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME);
        threadPoolTaskExecutor.initialize();

        return threadPoolTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    public static int getIdleThreadCnt() {
        int intWaitThreadCnt;

        try {
            intWaitThreadCnt = CORE_POOL_SIZE - (threadPoolTaskExecutor.getActiveCount());

            if (intWaitThreadCnt < 0) {
                intWaitThreadCnt = 0;
            }
        } catch(Exception ex) {
            intWaitThreadCnt = 0;
        }

        return intWaitThreadCnt;
    }
}