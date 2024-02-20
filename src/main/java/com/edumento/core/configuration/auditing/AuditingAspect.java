package com.edumento.core.configuration.auditing;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.edumento.core.constants.notification.Exchnages;
import com.edumento.core.constants.notification.RoutingKeys;
import com.edumento.core.exception.MintException;
import com.edumento.core.model.messages.audit.AuditMessage;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.security.SpringSecurityAuditorAware;

/** Created by ahmad on 4/13/16. updated by Ahmed salah 3/7/2017 */
@Aspect
public class AuditingAspect {

	private final Logger logger = LoggerFactory.getLogger(AuditingAspect.class);

	@Autowired
	SpringSecurityAuditorAware securityAuditorAware;

	// @Autowired RabbitTemplate rabbitTemplate;

	@Before("@annotation(auditable)")
	public void logBeforeAuditActivityBefore(JoinPoint jp, Auditable auditable) {
		var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		var currentUserDetail = SecurityUtils.getCurrentUser();
		var auditingUsernameIp = request.getHeader("X-Real-IP");
		if (auditingUsernameIp == null) {
			auditingUsernameIp = request.getRemoteAddr();
		}
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("AuditFlag", "before");
		dataMap.put("remoteIp", auditingUsernameIp);
		dataMap.put("Method", jp.toLongString());
		for (var i = 0; i < jp.getArgs().length; i++) {
			dataMap.put("Arg" + i, jp.getArgs()[i] != null ? jp.getArgs()[i].toString() : null);
		}

		var auditMessage = new AuditMessage(currentUserDetail != null ? currentUserDetail.getId() : null,
				currentUserDetail != null ? currentUserDetail.getUsername() : null,
				currentUserDetail != null ? currentUserDetail.getOrganizationId() : null,
				currentUserDetail != null ? currentUserDetail.getFoundationId() : null, auditable.value(), dataMap);
		auditMessage.setClientId(currentUserDetail != null ? currentUserDetail.getCurrentClientId() : null);
		send(Exchnages.MESSAGE_BUS, RoutingKeys.AUDIT, auditMessage);
	}

	@AfterThrowing(pointcut = "@annotation(auditable)", throwing = "ex")
	public void logAuditActivityAfterThrowing(JoinPoint jp, Auditable auditable, Exception ex) {
		if (ex instanceof MintException) {
			return;
		}
		var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		var currentUserDetail = SecurityUtils.getCurrentUser();
		var auditingUsernameIp = request.getHeader("X-Real-IP");
		if (auditingUsernameIp == null) {
			auditingUsernameIp = request.getRemoteAddr();
		}
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("AuditFlag", "afterThrowing");
		dataMap.put("remoteIp", auditingUsernameIp);
		dataMap.put("Method", jp.toLongString());
		for (var i = 0; i < jp.getArgs().length; i++) {
			dataMap.put("Arg" + i, jp.getArgs()[i] != null ? jp.getArgs()[i].toString() : null);
		}
		dataMap.put("Exception", ex.toString());
		var auditMessage = new AuditMessage(currentUserDetail != null ? currentUserDetail.getId() : null,
				currentUserDetail != null ? currentUserDetail.getUsername() : null,
				currentUserDetail != null ? currentUserDetail.getOrganizationId() : null,
				currentUserDetail != null ? currentUserDetail.getFoundationId() : null, auditable.value(), dataMap);
		auditMessage.setClientId(currentUserDetail != null ? currentUserDetail.getCurrentClientId() : null);

		send(Exchnages.MESSAGE_BUS, RoutingKeys.AUDIT, auditMessage);
	}

	@AfterReturning(pointcut = "@annotation(auditable)", returning = "retVal")
	public void logAuditActivityAfterReturning(JoinPoint jp, Object retVal, Auditable auditable) {
		var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		var currentUserDetail = SecurityUtils.getCurrentUser();
		var auditingUsernameIp = request.getHeader("X-Real-IP");
		if (auditingUsernameIp == null) {
			auditingUsernameIp = request.getRemoteAddr();
		}
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("AuditFlag", "afterReturn");
		dataMap.put("remoteIp", auditingUsernameIp);
		dataMap.put("Method", jp.toLongString());
		for (var i = 0; i < jp.getArgs().length; i++) {
			dataMap.put("Arg" + i, jp.getArgs()[i] != null ? jp.getArgs()[i].toString() : null);
		}
		dataMap.put("returnValue", String.valueOf(retVal));
		var auditMessage = new AuditMessage(currentUserDetail != null ? currentUserDetail.getId() : null,
				currentUserDetail != null ? currentUserDetail.getUsername() : null,
				currentUserDetail != null ? currentUserDetail.getOrganizationId() : null,
				currentUserDetail != null ? currentUserDetail.getFoundationId() : null, auditable.value(), dataMap);
		auditMessage.setClientId(currentUserDetail != null ? currentUserDetail.getCurrentClientId() : null);

		send(Exchnages.MESSAGE_BUS, RoutingKeys.AUDIT, auditMessage);
	}

	@Async
	public void send(String exchange, String routingKey, Object message) {
		// rabbitTemplate.setRoutingKey(routingKey);
		// rabbitTemplate.setExchange(exchange);
		// rabbitTemplate.convertAndSend(message);
	}
}
