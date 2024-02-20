package com.edumento.core.constants;

import java.util.HashMap;
import java.util.Map;

/** Created by ahmad on 5/18/16. */
public enum SpaceRole {

	// mint.space.community
	// mint.space.community.joinrequest
	// mint.space.content
	// mint.space.content.annotation.comment
	// mint.space.content.annotation
	// mint.space
	// mint.space.assessment.report
	// mint.space.assessment.solve
	// mint.space.assessment
	// mint.space.discussion.reply
	// mint.space.discussion

	VIEWER, COLLABORATOR, EDITOR, CO_OWNER, OWNER;

	private final Map<String, Byte> permissions = new HashMap<>();

	SpaceRole() {
		switch (name()) {
		case "VIEWER":
			getPermissions().put("mint.space", (byte) 1);
			getPermissions().put("mint.space.content", (byte) 1);

			break;
		case "COLLABORATOR":
			getPermissions().put("mint.space", (byte) 1);
			getPermissions().put("mint.space.content", (byte) 1);
			getPermissions().put("mint.space.discussion", (byte) 1);
			getPermissions().put("mint.space.inquiry", (byte) 15);
			getPermissions().put("mint.space.assessment", (byte) 1);
			getPermissions().put("mint.space.assessment.solve", (byte) 3);

			getPermissions().put("mint.space.content.annotation.comment", (byte) 15);
			getPermissions().put("mint.space.content.annotation", (byte) 15);
			getPermissions().put("mint.space.discussion.replay", (byte) 15);
			getPermissions().put("mint.space.inquiry.reply", (byte) 15);
			getPermissions().put("mint.space.community", (byte) 1);
			break;
		case "EDITOR":
			getPermissions().put("mint.space", (byte) 1);
			getPermissions().put("mint.space.content", (byte) 15);
			getPermissions().put("mint.space.assessment.report", (byte) 3);
			getPermissions().put("mint.space.discussion", (byte) 7);
			getPermissions().put("mint.space.inquiry", (byte) 15);
			getPermissions().put("mint.space.inquiry.reply", (byte) 15);
			getPermissions().put("mint.space.assessment", (byte) 1);
			getPermissions().put("mint.space.assessment.solve", (byte) 3);
			getPermissions().put("mint.space.content.annotation.comment", (byte) 15);
			getPermissions().put("mint.space.content.annotation", (byte) 15);
			getPermissions().put("mint.space.discussion.replay", (byte) 15);
			getPermissions().put("mint.space.content", (byte) 15);
			getPermissions().put("mint.space.community", (byte) 1);
			break;
		case "CO_OWNER":
			getPermissions().put("mint.space", (byte) 1);
			getPermissions().put("mint.space.content", (byte) 15);
			getPermissions().put("mint.space.discussion", (byte) 15);
			getPermissions().put("mint.space.assessment", (byte) 15);
			getPermissions().put("mint.space.assessment.solve", (byte) 3);
			getPermissions().put("mint.space.assessment.report", (byte) 1);
			getPermissions().put("mint.space.content.annotation.comment", (byte) 15);
			getPermissions().put("mint.space.content.annotation", (byte) 15);
			getPermissions().put("mint.space.discussion.replay", (byte) 15);
			getPermissions().put("mint.space.content", (byte) 15);
			getPermissions().put("mint.space.community", (byte) 15);
			getPermissions().put("mint.space.community.joinrequest", (byte) 15);
			getPermissions().put("mint.space.inquiry", (byte) 15);
			getPermissions().put("mint.space.inquiry.reply", (byte) 15);
			break;
		case "OWNER":
			getPermissions().put("mint.space", (byte) 15);
			getPermissions().put("mint.space.content", (byte) 15);
			getPermissions().put("mint.space.discussion", (byte) 15);
			getPermissions().put("mint.space.assessment", (byte) 15);
			getPermissions().put("mint.space.assessment.solve", (byte) 15);
			getPermissions().put("mint.space.assessment.report", (byte) 15);
			getPermissions().put("mint.space.content.annotation.comment", (byte) 15);
			getPermissions().put("mint.space.content.annotation", (byte) 15);
			getPermissions().put("mint.space.discussion.replay", (byte) 15);
			getPermissions().put("mint.space.content", (byte) 15);
			getPermissions().put("mint.space.community", (byte) 15);
			getPermissions().put("mint.space.community.joinrequest", (byte) 15);
			getPermissions().put("mint.space.inquiry", (byte) 15);
			getPermissions().put("mint.space.inquiry.reply", (byte) 15);
			break;
		}
	}

	public Map<String, Byte> getPermissions() {
		return permissions;
	}
}
