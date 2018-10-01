package org.tlh.springcloud.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author huping
 * @desc
 * @date 18/10/1
 */
@Slf4j
@Configuration
public class AsyncTaskExecutePool implements AsyncConfigurer {

    @Autowired
    private TaskThreadPoolConfigProperties taskThreadPoolConfigProperties;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskThreadPoolConfigProperties.getCorePoolSize());
        executor.setMaxPoolSize(taskThreadPoolConfigProperties.getMaxPoolSize());
        executor.setQueueCapacity(taskThreadPoolConfigProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(taskThreadPoolConfigProperties.getKeepAliveSeconds());
        executor.setThreadNamePrefix(taskThreadPoolConfigProperties.getThreadNamePrefix());
        //由调用线程处理该任务，线程池会将被拒绝的任务添加到”线程池正在运行的线程”中取运行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
