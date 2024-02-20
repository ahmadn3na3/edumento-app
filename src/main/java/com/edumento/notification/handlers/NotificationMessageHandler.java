package com.edumento.notification.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
//@RabbitListener(queues = "notificationqueue")
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
}
