package com.edumento.notification.service;

import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.notification.models.NotificationMessage;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

/**
 * Service for sending e-mails.
 *
 * <p>
 *
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 */
@Service
public class MailService {

	private final Logger log = LoggerFactory.getLogger(MailService.class);

	private final MessageSource messageSource;

	private final SpringTemplateEngine templateEngine;

	private final SendGrid sendGrid;

	@Value("${mint.enableMailNotification:false}")
	private boolean enableMailNotification;

	@Value("${mint.template:'mails'}")
	private String templateLocation;

	@Value("${mint.weburl:http://localhost:8080/}")
	private String weburl;

	@Value("${email-env-name:edumento}")
	private String emailEnvName;

	@Value("${normal-notifications-email:test@edumento.net}")
	private String normalNotificationsEmail;

	@Value("${announcment-email:test@edumento.net}")
	private String announcmentEmail;

	@Value("${forget-passeword-email:test@edumento.net}")
	private String forgetPasswordEmail;

	@Value("${resgisteration-email:test@edumento.net}")
	private String resgisterationEmail;

	@Autowired
	public MailService(MessageSource messageSource, SpringTemplateEngine templateEngine, SendGrid sendGrid) {
		this.messageSource = messageSource;
		this.templateEngine = templateEngine;
		this.sendGrid = sendGrid;
	}

	public void sendEmail(String from, String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart,
				isHtml, to, subject, content);

		Email fromEmail = new Email(from);
		Email toEmail = new Email(to);
		Content mailContent = new Content(MediaType.TEXT_HTML_VALUE, content);
		Mail mail = new Mail(fromEmail, subject, toEmail, mailContent);

		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sendGrid.api(request);
			log.debug("mail statusCode {}", response.getStatusCode());
			log.debug("mail body {}", response.getBody());
			log.debug("mail header {}", response.getHeaders());
		} catch (IOException ex) {
			log.error("E-mail could not be sent to user '{}', exception is: {}", to, ex.getMessage());
		}
	}

	@Async
	public void sendActivationEmail(UserInfoMessage user) {
		log.debug("Sending activation e-mail to '{}'", user.getEmail());
		Locale locale = Locale.forLanguageTag(user.getLang());
		Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("weburl", weburl);
		String content;
		if (user.getLang() != null && user.getLang().toLowerCase().contains("ar")) {
			content = templateEngine.process(templateLocation + "/activate_mail_ar", context);
		} else {
			content = templateEngine.process(templateLocation + "/activate_mail_en", context);
		}
		String subject = messageSource.getMessage(emailEnvName + " " + "email.creation.title", null,
				emailEnvName + " user creation", locale);
		sendEmail(resgisterationEmail, user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendCreationEmail(UserInfoMessage user) {
		log.debug("Sending creation e-mail to '{}'", user.getEmail());
		Locale locale = Locale.forLanguageTag(user.getLang());
		Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("password", user.getPassword());
		context.setVariable("weburl", weburl);
		String content;
		if (user.getLang() != null && user.getLang().toLowerCase().contains("ar")) {
			content = templateEngine.process(templateLocation + "/creation_mail_ar", context);
		} else {
			content = templateEngine.process(templateLocation + "/creation_mail_en", context);
		}
		String subject = messageSource.getMessage(emailEnvName + " " + "email.activation.title", null,
				emailEnvName + " Activation Mail", locale);
		log.info("Mail Content Is {}", content);
		sendEmail(resgisterationEmail, user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendPasswordResetMail(UserInfoMessage user) {
		log.debug("Sending password reset e-mail to '{}'", user.getEmail());
		Locale locale = Locale.forLanguageTag(user.getLang());
		Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("weburl", weburl);
		String content;
		if (user.getLang() != null && user.getLang().toLowerCase().contains("ar")) {
			content = templateEngine.process(templateLocation + "/reset_mail_ar", context);
		} else {
			content = templateEngine.process(templateLocation + "/reset_mail_en", context);
		}

		String subject = messageSource.getMessage(emailEnvName + " " + "email.reset.title", null,
				emailEnvName + " Reset Passeword Mail", locale);
		sendEmail(forgetPasswordEmail, user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendNotificationMail(NotificationMessage notificationMessage, UserInfoMessage user, boolean withImage,
			boolean useTargeImage) {

		if (!enableMailNotification) {
			return;
		}
		log.debug("Sending Notification e-mail to '{}'", user.getEmail());

		Locale locale = Locale.forLanguageTag(user.getLang());
		Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("withImage", withImage);
		context.setVariable("weburl", weburl);
		if (withImage && useTargeImage) {
			context.setVariable("image", notificationMessage.getTarget().getImage());
		} else if (withImage) {
			context.setVariable("image", notificationMessage.getFrom().getImage());
		}
		context.setVariable("from", notificationMessage.getFrom().getName());
		context.setVariable("actionMessage", notificationMessage.getMessage());
		context.setVariable("date", String.format("%1$td/%1$tm/%1$tY", notificationMessage.getDate()));
		String content;
		if (user.getLang() != null && user.getLang().toLowerCase().contains("ar")) {
			content = templateEngine.process(templateLocation + "/notification_email_ar", context);
		} else {
			content = templateEngine.process(templateLocation + "/notification_email_en", context);
		}

		String subject = messageSource.getMessage("email.notification.title", null, "notification mail", locale);
		sendEmail(normalNotificationsEmail, user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendChallengeMail(NotificationMessage notificationMessage, UserInfoMessage user, boolean withImage,
			boolean useTargeImage) {

		if (!enableMailNotification) {
			return;
		}
		log.debug("Sending Notification e-mail to '{}'", user.getEmail());

		Locale locale = Locale.forLanguageTag(user.getLang());
		Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("withImage", withImage);
		context.setVariable("weburl", weburl);
		if (withImage && useTargeImage) {
			context.setVariable("image", notificationMessage.getTarget().getImage());
		} else if (withImage) {
			context.setVariable("image", notificationMessage.getFrom().getImage());
		}
//		context.setVariable("from", notificationMessage.getFrom().getName());
		context.setVariable("actionMessage", notificationMessage.getMessage());
		context.setVariable("date", String.format("%1$td/%1$tm/%1$tY", notificationMessage.getDate()));
		String content;
		if (user.getLang() != null && user.getLang().toLowerCase().contains("ar")) {
			content = templateEngine.process(templateLocation + "/notification_email_ar", context);
		} else {
			content = templateEngine.process(templateLocation + "/notification_email_en", context);
		}

		String subject = messageSource.getMessage("email.notification.title", null, "notification mail", locale);
		sendEmail(normalNotificationsEmail, user.getEmail(), subject, content, false, true);
	}

	@Async
	public void sendAnnouncementMail(NotificationMessage notificationMessage, UserInfoMessage user, boolean withImage,
			boolean useTargeImage) {

		if (!enableMailNotification) {
			return;
		}
		log.debug("Sending Announcement e-mail to '{}'", user.getEmail());

		Locale locale = Locale.forLanguageTag(user.getLang());
		Context context = new Context(locale);
		context.setVariable("user", user);
		context.setVariable("weburl", weburl);
		context.setVariable("from", notificationMessage.getFrom().getName());
		context.setVariable("actionMessage", notificationMessage.getMessage());
		context.setVariable("body", notificationMessage.getBody());
		context.setVariable("date", String.format("%1$td/%1$tm/%1$tY", notificationMessage.getDate()));
		String content;
		if (user.getLang() != null && user.getLang().toLowerCase().contains("ar")) {
			content = templateEngine.process(templateLocation + "/announcement_email_ar", context);
		} else {
			content = templateEngine.process(templateLocation + "/announcement_email_en", context);
		}

		String subject = notificationMessage.getMessage();
		sendEmail(announcmentEmail, user.getEmail(), subject, content, false, true);
	}

	public String getEmailEnvName() {
		return emailEnvName;
	}

	public void setEmailEnvName(String emailEnvName) {
		this.emailEnvName = emailEnvName;
	}

}
