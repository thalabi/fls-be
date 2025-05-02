package com.kerneldc.fls.springconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private static final int POOL_SIZE = 10;
    private static final String THREAD_NAME_PREFIX = "scheduled-task-pool-";
    
    /**
     * Configures the scheduler to allow multiple threads.
     *
     * @param taskRegistrar The task register.
     */
//	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

		threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
		threadPoolTaskScheduler.setThreadNamePrefix(THREAD_NAME_PREFIX);
		threadPoolTaskScheduler.initialize();
		
		taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
	}

}
