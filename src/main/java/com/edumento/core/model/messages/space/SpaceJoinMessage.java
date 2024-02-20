package com.edumento.core.model.messages.space;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.edumento.core.constants.JoinedStatus;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.user.UserInfoMessage;

public class SpaceJoinMessage extends SpaceInfoMessage {
	private UserInfoMessage joinedInfoMessage;
	private JoinedStatus joinedStatus;

	public SpaceJoinMessage() {
	}

	public SpaceJoinMessage(Long id, String name, String image, From from, String categoryName, String categoryNameAR,
			Boolean isPrivate, UserInfoMessage joinedInfoMessage, JoinedStatus joinedStatus, String chatId) {
		super(id, name, image, from, categoryName, categoryNameAR, isPrivate, chatId);
		this.joinedInfoMessage = joinedInfoMessage;
		this.joinedStatus = joinedStatus;
	}

	public UserInfoMessage getJoinedInfoMessage() {
		return joinedInfoMessage;
	}

	public void setJoinedInfoMessage(UserInfoMessage joinedInfoMessage) {
		this.joinedInfoMessage = joinedInfoMessage;
	}

	public JoinedStatus getJoinedStatus() {
		return joinedStatus;
	}

	public void setJoinedStatus(JoinedStatus joinedStatus) {
		this.joinedStatus = joinedStatus;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("joinedInfoMessage", joinedInfoMessage)
				.append("joinedStatus", joinedStatus).toString();
	}
}
