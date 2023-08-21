package com.edumento.core.configuration;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.edumento.core.async.ExceptionHandlingAsyncTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer {

	private final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

	@Autowired
	private MintProperties mintProperties;

	@Override
	@Bean(name = "taskExecutor")
	public Executor getAsyncExecutor() {
		log.debug("Creating Async Task Executor");
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(mintProperties.getAsync().getCorePoolSize());
		executor.setMaxPoolSize(mintProperties.getAsync().getMaxPoolSize());
		executor.setQueueCapacity(mintProperties.getAsync().getQueueCapacity());
		executor.setThreadNamePrefix("edumento-Executor-");
		return new ExceptionHandlingAsyncTaskExecutor(executor);
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}

	// @Bean
	// public TaskScheduler taskScheduler() {
	// return new ConcurrentTaskScheduler();
	// }

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(mintProperties.getAsync().getMaxPoolSize());
		threadPoolTaskScheduler.setThreadNamePrefix("AutoSolvingTaskScheduler");
		return threadPoolTaskScheduler;
	}
}
