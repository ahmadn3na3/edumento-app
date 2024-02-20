package com.edumento.notification.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sendgrid.SendGrid;

@Configuration
public class SendGridConfiguration {

	@Value("${mint.sendGrid.apikey:000000000}")
	private String sendGridApiKey;

	@Bean
	public SendGrid createSendGridCreate() {
		return new SendGrid(sendGridApiKey);
	}
}
