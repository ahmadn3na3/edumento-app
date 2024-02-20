package com.edumento.notification.handlers.impl;

import static com.edumento.core.constants.notification.Actions.CREATE;
import static com.edumento.core.constants.notification.EntityType.ASSESSMENTS;
import static com.edumento.core.constants.notification.MessageCategory.USER;

import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edumento.assessment.repos.AssessmentRepository;
import com.edumento.core.constants.AssessmentType;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.Actions;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.Target;
import com.edumento.core.model.messages.assessment.AssessementsInfoMessage;
import com.edumento.core.model.messages.assessment.AssessmentSubmitMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.service.MailService;
import com.edumento.notification.util.Utilities;
import com.edumento.space.domain.Joined;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
@Component
public class AssessmentNotificationHandler extends AbstractHandler {

	private final Utilities utilities;

	@Autowired
	public AssessmentNotificationHandler(UserRepository userRepository, AmqNotifier amqNotifier,
			MailService mailService, ObjectMapper objectMapper, AssessmentRepository assessmentRepository,
			Utilities utilities) {
		super(userRepository, amqNotifier, mailService, objectMapper);
		this.utilities = utilities;
	}

	@Override
	protected void handleNonCRUDAction(BaseMessage notificationMessage) {
		switch (notificationMessage.getEntityAction().getAction()) {
		case Actions.SUBMITTED:
			onSubmit(notificationMessage);
			break;

		default:
			break;
		}
	}

	private void onSubmit(BaseMessage baseMessage) {
		var assessmentSubmitMessage = mapJsonObject(baseMessage, AssessmentSubmitMessage.class);
		var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), USER,
				assessmentSubmitMessage.getFrom(),
				new Target(ASSESSMENTS, assessmentSubmitMessage.getId().toString(), Actions.SUBMITTED));
		final String message;
		switch (assessmentSubmitMessage.getAssessmentType()) {
		case QUIZ:
			message = assessmentSubmitMessage.getIsOwner().booleanValue()
					? "mint.notification.message.assessment.quiz.graded"
					: "mint.notification.message.assessment.quiz.solved";
			break;
		case ASSIGNMENT:
			message = assessmentSubmitMessage.getIsOwner().booleanValue()
					? "mint.notification.message.assessment.assignment.graded"
					: "mint.notification.message.assessment.assignment.solved";
			break;
		case PRACTICE:
			return;
		case WORKSHEET:
			message = assessmentSubmitMessage.getIsOwner().booleanValue()
					? "mint.notification.message.assessment.worksheet.graded"
					: "mint.notification.message.assessment.worksheet.solved";
			break;
		default:
			message = null;
			break;
		}

		var userInfoMessage = !assessmentSubmitMessage.getIsOwner().booleanValue()
				? assessmentSubmitMessage.getOwner()
				: assessmentSubmitMessage.getUserSolved();
		var joined = utilities.getJoinedUser(userInfoMessage.getId(), assessmentSubmitMessage.getSpaceId());
		var notificationMessage = amqNotifier.saveMessage(userInfoMessage, baseNotificationMessage,
				message, null, assessmentSubmitMessage.getName(), assessmentSubmitMessage.getSpaceName(),
				assessmentSubmitMessage.getCategoryName());

		if (joined.getNotification() && joined.getUser().getNotification()) {
			amqNotifier.send(notificationMessage);
			if (Boolean.TRUE.equals(joined.getUser().getMailNotification())) {
				mailService.sendNotificationMail(notificationMessage, userInfoMessage, true, false);
			}
		}
	}

	@Override
	protected void onCreate(BaseMessage baseMessage) {
		var assessementsInfoMessage = mapJsonObject(baseMessage, AssessementsInfoMessage.class);

		var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), USER,
				assessementsInfoMessage.getFrom(),
				new Target(ASSESSMENTS, assessementsInfoMessage.getId().toString(), CREATE));
		final String message;
		switch (assessementsInfoMessage.getAssessmentType()) {
		case QUIZ:
			message = "mint.notification.message.assessment.quiz.create";
			onCreateSendMessage(assessementsInfoMessage, baseNotificationMessage, message);
			break;
		case ASSIGNMENT:
			message = "mint.notification.message.assessment.assignment.create";
			onCreateSendMessage(assessementsInfoMessage, baseNotificationMessage, message);
			break;
		case PRACTICE:
			return;
		case WORKSHEET:
			message = "mint.notification.message.assessment.worksheet.create";
			onCreateSendMessage(assessementsInfoMessage, baseNotificationMessage, message);
			break;
		case CHALLENGE:
			message = "mint.notification.message.assessment.challenge.create";
			var infoMessage = new UserInfoMessage(userRepository
					.findById(assessementsInfoMessage.getChallengeeId()).orElseThrow(NotFoundException::new));
			var notificationMessage = amqNotifier.saveMessage(infoMessage, baseNotificationMessage,
					message, null, assessementsInfoMessage.getFrom().getName(), assessementsInfoMessage.getSpaceName(),
					String.format("%1$td/%1$tm/%1$tY %1$tr %1$tZ", assessementsInfoMessage.getDueDateTime()));
			if (infoMessage.getNotification()) {
				amqNotifier.send(notificationMessage);
			}
			if (infoMessage.getMailNotification()) {
				mailService.sendChallengeMail(notificationMessage, infoMessage, true, false);
			}
			break;
		default:
			message = null;
			break;
		}
	}

	private void onCreateSendMessage(AssessementsInfoMessage assessementsInfoMessage,
			BaseNotificationMessage baseNotificationMessage, String message) {
		utilities.getCommunityUserList(assessementsInfoMessage.getSpaceId(), assessementsInfoMessage.getFrom().getId())
				.stream()
				.filter(new Predicate<Joined>() {
					@Override
					public boolean test(Joined joined) {
						return joined.getSpaceRole() != SpaceRole.VIEWER
								&& !joined.getUser().getId().equals(assessementsInfoMessage.getFrom().getId());
					}
				})
				.forEach(new Consumer<Joined>() {
					@Override
					public void accept(Joined joined) {
						var infoMessage = new UserInfoMessage(joined.getUser());
						var notificationMessage = amqNotifier.saveMessage(infoMessage,
								baseNotificationMessage, message, null, assessementsInfoMessage.getName(),
								assessementsInfoMessage.getSpaceName(), assessementsInfoMessage.getCategoryName(),
								assessementsInfoMessage.getAssessmentType() == AssessmentType.QUIZ
										? String.format("%1$td/%1$tm/%1$tY %1$tr %1$tZ",
												assessementsInfoMessage.getStartDateTime())
										: String.format("%1$td/%1$tm/%1$tY %1$tr %1$tZ",
												assessementsInfoMessage.getDueDateTime()));
						if (joined.getUser().getNotification() && joined.getNotification()) {
							amqNotifier.send(notificationMessage);
						}
						if (Boolean.TRUE.equals(joined.getUser().getMailNotification())) {
							mailService.sendNotificationMail(notificationMessage, infoMessage, true, false);
						}
					}
				});
	}

}
