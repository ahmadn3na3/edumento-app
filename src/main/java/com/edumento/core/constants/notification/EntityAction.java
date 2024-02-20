package com.edumento.core.constants.notification;

import java.util.Collection;

import com.edumento.core.model.messages.UserFollowMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;

/** Created by ahmad on 5/29/17. */
public enum EntityAction {
	QUESTION_CREATE(EntityType.QUESTION, Actions.CREATE, String.class),
	QUESTION_DELETE(EntityType.QUESTION, Actions.DELETE, String.class),
	QUESTION_DELETE_BULK(EntityType.QUESTION, Actions.DELETE, Collection.class),
	QUESTION_CREATE_BULK(EntityType.QUESTION, Actions.CREATE, Collection.class),
	QUESTION_UPDATE(EntityType.QUESTION, Actions.UPDATE, String.class),
	SPACE_UPDATE(EntityType.SPACE, Actions.UPDATE, Long.class),
	SPACE_CREATE(EntityType.SPACE, Actions.CREATE, Long.class),
	SPACE_DELETE(EntityType.SPACE, Actions.DELETE, Long.class), SPACE_RATE(EntityType.SPACE, Actions.RATE, Long.class),
	SPACE_SHARE(EntityType.SPACE, Actions.SHARED, Long.class),
	SPACE_UNSHARE(EntityType.SPACE, Actions.UNSHARED, Long.class),
	SPACE_JOIN_REQUEST(EntityType.SPACE, Actions.JOINEREQUEST, Long.class),
	SPACE_FAVORIT(EntityType.SPACE, Actions.FAVORIT, Long.class),
	SPACE_UNFAVORIT(EntityType.SPACE, Actions.FAVORIT, Long.class),
	SPACE_LEAVE(EntityType.SPACE, Actions.LEAVE, Long.class),
	CATEGORY_UPDATE(EntityType.CATEGORY, Actions.UPDATE, Long.class),
	CATEGORY_CREATE(EntityType.CATEGORY, Actions.CREATE, Long.class),
	CATEGORY_DELETE(EntityType.CATEGORY, Actions.DELETE, Long.class),
	USER_UPDATE(EntityType.USER, Actions.UPDATE, Long.class), USER_DELETE(EntityType.USER, Actions.DELETE, Long.class),
	USER_FOLLOW(EntityType.USER, Actions.FOLLOW, UserFollowMessage.class),
	USER_BLOCK(EntityType.USER, Actions.BLOCK, Long.class), ROLE_UPDATE(EntityType.ROLE, Actions.UPDATE, Long.class),
	ROLE_CREATE(EntityType.ROLE, Actions.CREATE, Long.class), ROLE_DELETE(EntityType.ROLE, Actions.DELETE, Long.class),
	ROLE_ASSIGN(EntityType.ROLE, Actions.ASSIGN, Long.class),
	ROLE_UNASSIGN(EntityType.ROLE, Actions.UNASSIGN, Long.class),
	CONTENT_CREATE(EntityType.CONTENT, Actions.CREATE, Long.class),
	CONTENT_UPDATE(EntityType.CONTENT, Actions.UPDATE, Long.class),
	CONTENT_DELETE(EntityType.CONTENT, Actions.DELETE, Long.class),
	ANNOTATION_CREATE(EntityType.ANNOTATIONS, Actions.CREATE, String.class),
	ANNOTATION_DELETE(EntityType.ANNOTATIONS, Actions.DELETE, String.class),
	ANNOTATION_LIKE(EntityType.ANNOTATIONS, Actions.LIKE, String.class),
	ANNOTATION_UPDATE(EntityType.ANNOTATIONS, Actions.UPDATE, String.class),
	ANNOTATION_COMMENT_CREATE(EntityType.ANNOTATION_REPLY, Actions.CREATE, String.class),
	ANNOTATION_COMMENT_LIKE(EntityType.ANNOTATION_REPLY, Actions.LIKE, String.class),
	ANNOTATION_COMMENT_DELETE(EntityType.ANNOTATION_REPLY, Actions.DELETE, String.class),
	ANNOTATION_COMMENT_UPDATE(EntityType.ANNOTATION_REPLY, Actions.UPDATE, String.class),
	DISCUSSION_CREATE(EntityType.DISCUSSION, Actions.CREATE, String.class),
	INQUERY_CREATE(EntityType.INQUERY, Actions.CREATE, String.class),
	INQUERY_COMMENT_CREATE(EntityType.INQUERY_REPLY, Actions.CREATE, String.class),
	DISCUSSION_DELETE(EntityType.DISCUSSION, Actions.DELETE, String.class),
	DISCUSSION_LIKE(EntityType.DISCUSSION, Actions.LIKE, String.class),
	DISCUSSION_COMMENT_CREATE(EntityType.DISCUSSION_REPLY, Actions.CREATE, String.class),
	DISCUSSION_COMMENT_LIKE(EntityType.DISCUSSION_REPLY, Actions.LIKE, String.class),
	DISCUSSION_COMMENT_DELETE(EntityType.DISCUSSION_REPLY, Actions.DELETE, String.class),
	DISCUSSION_COMMENT_UPDATE(EntityType.DISCUSSION_REPLY, Actions.UPDATE, String.class),
	TIME_LOCK_UPDATE(EntityType.TIME_LOCK, Actions.UPDATE, Long.class),
	TIME_LOCK_CREATE(EntityType.TIME_LOCK, Actions.CREATE, Long.class),
	TIME_LOCK_DELETE(EntityType.TIME_LOCK, Actions.DELETE, Long.class),
	ASSESSMENT_CREATE(EntityType.ASSESSMENTS, Actions.CREATE, Long.class),
	ASSESSMENT_SUBMIT(EntityType.ASSESSMENTS, Actions.SUBMITTED, Long.class),
	ASSESSMENT_UPDATE(EntityType.ASSESSMENTS, Actions.UPDATE, Long.class),
	ASSESSMENT_DELETE(EntityType.ASSESSMENTS, Actions.DELETE, Long.class),
	ASSESSMENT_PUBLISH(EntityType.ASSESSMENTS, Actions.PUBLISH, Long.class),
	USER_REGISTER(EntityType.USER, Actions.CREATE, UserInfoMessage.class),
	USER_CREATE(EntityType.USER, Actions.CREATE, UserInfoMessage.class),
	USER_FORGETPASSOWORD(EntityType.USER, Actions.UPDATE, UserInfoMessage.class),
	USER_ACTIVATE(EntityType.USER, Actions.UPDATE, UserInfoMessage.class),
	USER_REACTIVATE(EntityType.USER, Actions.UPDATE, UserInfoMessage.class),
	FOUNDATION_CREATE(EntityType.FOUNDATION, Actions.CREATE, Long.class),
	FOUNDATION_UPDATE(EntityType.FOUNDATION, Actions.UPDATE, Long.class),
	FOUNDATION_DELETE(EntityType.FOUNDATION, Actions.DELETE, Long.class),
	GROUP_CREATE(EntityType.GROUP, Actions.CREATE, Long.class),
	GROUP_DELETE(EntityType.GROUP, Actions.DELETE, Long.class),
	GROUP_UPDATE(EntityType.GROUP, Actions.UPDATE, Long.class),
	REPORT_POST(EntityType.REPORT, Actions.POST, String.class),
	ORGANIZATION_CREATE(EntityType.ORGANIZATION, Actions.CREATE, Long.class),
	ORGANIZATION_UPDATE(EntityType.ORGANIZATION, Actions.CREATE, Long.class),
	ORGANIZATION_DELETE(EntityType.ORGANIZATION, Actions.CREATE, Long.class),
	SPACE_JOIN(EntityType.SPACE, Actions.JOIN, Long.class),
	SPACE_JOIN_ACCEPT(EntityType.SPACE, Actions.ACCEPTED, Long.class),
	ANNOUNCEMENT_CREATE(EntityType.ANNOUNCEMENT, Actions.CREATE, String.class);

	private final Integer action;
	private final Integer entity;
	private final Class<?> idClass;
	private final String messageSuffix;

	EntityAction(Integer entity, Integer action, Class<?> idClass) {
		this.action = action;
		this.entity = entity;
		this.idClass = idClass;
		messageSuffix = name().toLowerCase().replace('_', '.');
	}

	public Integer getAction() {
		return action;
	}

	public Integer getEntity() {
		return entity;
	}

	public Class<?> getIdClass() {
		return idClass;
	}

	public String getMessageSuffix() {
		return messageSuffix;
	}
}
