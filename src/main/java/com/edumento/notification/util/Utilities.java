package com.edumento.notification.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.UserRelationType;
import com.edumento.space.domain.Joined;
import com.edumento.space.domain.UserRelation;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.UserRelationRepository;
import com.edumento.user.domain.User;

/** Created by ayman on 01/08/17. */
@Component
public class Utilities {
	@Autowired
	JoinedRepository joinedRepository;

	@Autowired
	UserRelationRepository userRelationRepository;

	public List<Joined> getCommunityUserList(Long spaceId, Long filter) {
		return joinedRepository.findBySpaceIdAndDeletedFalse(spaceId)
				.filter(new Predicate<Joined>() {
					@Override
					public boolean test(Joined joined) {
						return !joined.getUser().getId().equals(filter);
					}
				}).collect(Collectors.toList());
	}

	public List<Joined> getDeletedCommunityUserList(Long spaceId, Long filter) {
		return joinedRepository.findBySpaceIdAndDeletedTrue(spaceId)
				.filter(new Predicate<Joined>() {
					@Override
					public boolean test(Joined joined) {
						return !joined.getUser().getId().equals(filter);
					}
				}).collect(Collectors.toList());
	}

	public List<User> getFollowerList(Long userId) {
		return userRelationRepository.findByUserIdAndRelationTypeAndDeletedFalse(userId, UserRelationType.FOLLOWER)
				.map(UserRelation::getFollow).collect(Collectors.toList());
	}

	public User getSpaceOwner(Long spaceId) {
		return joinedRepository.getSpaceCommunity(spaceId).filter(new Predicate<Joined>() {
			@Override
			public boolean test(Joined joined) {
				return joined.getSpaceRole() == SpaceRole.OWNER;
			}
		})
				.findFirst().map(Joined::getUser).orElseGet(new Supplier<User>() {
					@Override
					public User get() {
						return null;
					}
				});
	}

	public Joined getJoinedUser(Long spaceId, Long userId) {
		return joinedRepository.findOneByUserIdAndSpaceIdAndDeletedFalse(userId, spaceId).orElse(null);
	}
}
