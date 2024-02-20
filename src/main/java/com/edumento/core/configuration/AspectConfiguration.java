package com.edumento.core.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.edumento.core.configuration.auditing.AuditingAspect;
import com.edumento.core.configuration.logging.LoggingAspect;
import com.edumento.core.configuration.notifications.MessageAspect;

/** Created by ahmad on 4/13/16. */
@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {
	@Bean
	@ConditionalOnProperty(name = "mint.enableAuditing", havingValue = "true")
	public AuditingAspect auditingAspect() {
		return new AuditingAspect();
	}

	@Bean
	public LoggingAspect loggingAspect() {
		return new LoggingAspect();
	}

	@Bean
	public MessageAspect messageAspect() {
		return new MessageAspect();
	}
}
