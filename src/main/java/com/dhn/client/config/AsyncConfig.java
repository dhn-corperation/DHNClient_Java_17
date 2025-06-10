package com.dhn.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {
    // KAO 작업용 Executor
    @Bean(name = "kaoTaskExecutor")
    public Executor kaoTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("KAO-task-");
        executor.initialize();
        return executor;
    }

    // FT 작업용 Executor
    @Bean(name = "ftTaskExecutor")
    public Executor ftTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("FT-task-");
        executor.initialize();
        return executor;
    }

    // SMS 작업용 Executor
    @Bean(name = "smsTaskExecutor")
    public Executor smsTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("SMS-task-");
        executor.initialize();
        return executor;
    }

    // LMS 작업용 Executor
    @Bean(name = "lmsTaskExecutor")
    public Executor lmsTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("LMS-task-");
        executor.initialize();
        return executor;
    }

    // MMS 작업용 Executor
    @Bean(name = "mmsTaskExecutor")
    public Executor mmsTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("MMS-task-");
        executor.initialize();
        return executor;
    }
}
