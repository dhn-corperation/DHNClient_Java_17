package com.dhn.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@EnableAsync
@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
	private final int POOL_SIZE = 15;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// TODO Auto-generated method stub
		ThreadPoolTaskScheduler tpts = new ThreadPoolTaskScheduler();

		tpts.setPoolSize(POOL_SIZE);
		tpts.setThreadNamePrefix("DHN-sch-task-pool-");
		tpts.initialize();

		taskRegistrar.setTaskScheduler(tpts);
	}

}
