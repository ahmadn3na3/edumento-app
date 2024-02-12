package com.edumento.notification.handlers;

import static com.edumento.core.constants.notification.EntityType.ANNOTATIONS;
import static com.edumento.core.constants.notification.EntityType.ANNOTATION_REPLY;
import static com.edumento.core.constants.notification.EntityType.ASSESSMENTS;
import static com.edumento.core.constants.notification.EntityType.CATEGORY;
import static com.edumento.core.constants.notification.EntityType.CONTENT;
import static com.edumento.core.constants.notification.EntityType.DISCUSSION;
import static com.edumento.core.constants.notification.EntityType.DISCUSSION_REPLY;
import static com.edumento.core.constants.notification.EntityType.PERMISSION;
import static com.edumento.core.constants.notification.EntityType.QUESTION;
import static com.edumento.core.constants.notification.EntityType.ROLE;
import static com.edumento.core.constants.notification.EntityType.SPACE;
import static com.edumento.core.constants.notification.EntityType.TIME_LOCK;
import static com.edumento.core.constants.notification.EntityType.USER;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.edumento.core.exception.InvalidException;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.notification.handlers.impl.AnnotationRelplyHandler;
import com.edumento.notification.handlers.impl.AssessmentNotificationHandler;
import com.edumento.notification.handlers.impl.CategoryNotificationHandler;
import com.edumento.notification.handlers.impl.ContentHandler;
import com.edumento.notification.handlers.impl.DiscussionHadler;
import com.edumento.notification.handlers.impl.DiscussionReplyHandler;
import com.edumento.notification.handlers.impl.PermissionHandler;
import com.edumento.notification.handlers.impl.QuestionHandler;
import com.edumento.notification.handlers.impl.RoleNotificationHandler;
import com.edumento.notification.handlers.impl.SpaceNotificationHandler;
import com.edumento.notification.handlers.impl.TimeLockNotificationHandler;
import com.edumento.notification.handlers.impl.UserHandler;


/** Created by ayman on 01/03/17. */
@Component
@RabbitListener(queues = "notificationqueue")
public class NotificationMessageHandler {

	@Autowired
	SpaceNotificationHandler spaceNotificationHandler;

	@Autowired
	UserHandler userHandler;

//	@Autowired
//	AnnotationHandler annotationHandler;

	@Autowired
	AnnotationRelplyHandler annotationRelplyHandler;
	
//	@Autowired
//	AnnouncementHandler announcementHandler;

	@Autowired
	DiscussionHadler discussionHadler;

	@Autowired
	PermissionHandler permissionHandler;

	@Autowired
	QuestionHandler questionHandler;

	@Autowired
	RoleNotificationHandler roleNotificationHandler;

	@Autowired
	TimeLockNotificationHandler timeLockNotificationHandler;

	@Autowired
	DiscussionReplyHandler discussionReplyHandler;

	@Autowired
	ContentHandler contentHandler;

	@Autowired
	CategoryNotificationHandler categoryNotificationHandler;

	@Autowired
	AssessmentNotificationHandler assessmentNotificationHandler;

	@RabbitHandler
	private void handle(@Payload BaseMessage notificationMessage) {
		int entityType = -1;
		if (null != notificationMessage) {
			entityType = notificationMessage.getEntityAction().getEntity();
		}
		switch (entityType) {
		case SPACE:
			spaceNotificationHandler.handle(notificationMessage);
			break;
		case USER:
			userHandler.handle(notificationMessage);
			break;
		case CONTENT:
			contentHandler.handle(notificationMessage);
			break;
//		case ANNOTATIONS:
//			annotationHandler.handle(notificationMessage);
//			break;
		case DISCUSSION:
			discussionHadler.handle(notificationMessage);
			break;
//		case ANNOUNCEMENT:
//			announcementHandler.handle(notificationMessage);
//			break;
		case ANNOTATION_REPLY:
			annotationRelplyHandler.handle(notificationMessage);
			break;
		case PERMISSION:
			permissionHandler.handle(notificationMessage);
			break;
		case QUESTION:
			questionHandler.handle(notificationMessage);
			break;
		case ROLE:
			roleNotificationHandler.handle(notificationMessage);
			break;
		case TIME_LOCK:
			timeLockNotificationHandler.handle(notificationMessage);
			break;
		case ASSESSMENTS:
			assessmentNotificationHandler.handle(notificationMessage);
			break;
		case DISCUSSION_REPLY:
			discussionReplyHandler.handle(notificationMessage);
			break;
		case CATEGORY:
			categoryNotificationHandler.handle(notificationMessage);
			break;
		default:
			throw new InvalidException();
		}
	}
}
