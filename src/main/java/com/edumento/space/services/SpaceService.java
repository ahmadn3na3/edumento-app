package com.edumento.space.services;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.assessment.domain.Assessment;
import com.edumento.assessment.repos.AssessmentRepository;

import com.edumento.content.domain.Content;
import com.edumento.content.repos.ContentRepository;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.JoinedStatus;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.SortField;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.space.SpaceInfoMessage;
import com.edumento.core.model.messages.space.SpaceJoinMessage;
import com.edumento.core.model.messages.space.SpaceShareInfoMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.core.util.RandomUtils;
import com.edumento.discussion.domain.Discussion;
import com.edumento.discussion.repos.CommentRepository;
import com.edumento.discussion.repos.DiscussionRepository;
import com.edumento.space.domain.Joined;
import com.edumento.space.domain.Space;
import com.edumento.space.mappers.SpaceMapper;
import com.edumento.space.model.space.request.SpaceCreateModel;
import com.edumento.space.model.space.request.SpaceRoleModel;
import com.edumento.space.model.space.request.SpaceShareModel;
import com.edumento.space.model.space.response.SpaceListingModel;
import com.edumento.space.model.space.response.SpaceListingUpdateModel;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.SpaceRepository;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.model.user.UserInfoModel;
import com.edumento.user.repo.UserRepository;

/** Created by ahmad on 3/2/16. */
@Service
public class SpaceService {
	private final Logger log = LoggerFactory.getLogger(SpaceService.class);

	private final SpaceRepository spaceRepository;

	private final JoinedRepository joinedRepository;

	private final UserRepository userRepository;

	private final ContentRepository contentRepository;

	private final AssessmentRepository assessmentRepository;

	private final DiscussionRepository discussionRepository;

	private final CommentRepository commentRepository;

	@Value("${mint.url}")
	private String url;

	@Value("${spring.profiles.active:dev}")
	private String profile;

	@Autowired
	public SpaceService(JoinedRepository joinedRepository, UserRepository userRepository,
			SpaceRepository spaceRepository,
			ContentRepository contentRepository, AssessmentRepository assessmentRepository,
			DiscussionRepository discussionRepository, CommentRepository commentRepository) {
		this.joinedRepository = joinedRepository;
		this.userRepository = userRepository;
		this.spaceRepository = spaceRepository;
		this.contentRepository = contentRepository;
		this.assessmentRepository = assessmentRepository;
		this.discussionRepository = discussionRepository;
		this.commentRepository = commentRepository;
	}

	@Transactional
	@Auditable(EntityAction.SPACE_CREATE)
	@PreAuthorize("hasAuthority('SPACE_CREATE')")
	@Message(entityAction = EntityAction.SPACE_CREATE, services = { Services.NOTIFICATIONS,
			Services.CHAT }, withModel = true, indexOfModel = 0)
	public ResponseModel createSpaceForUser(SpaceCreateModel createModel, String username) {
		final User user = userRepository.findOneByUserNameAndDeletedFalse(username)
				.orElseThrow(NotPermittedException::new);

		if (spaceRepository.countByNameAndUserIdAndDeletedFalse(createModel.getName(), user.getId()) > 0) {
			throw new ExistException(createModel.getName());
		}

		var space = new Space(createModel.getName(),
				Arrays.toString(createModel.getTags() != null ? createModel.getTags().toArray() : new String[0]),
				createModel.getPrice(), createModel.getPaid(), createModel.getIsPrivate(), createModel.getImage(),
				createModel.getDescription());
		space.setColor(createModel.getColor() == null ? RandomUtils.genertateRandomColor() : createModel.getColor());
		space.setJoinRequestsAllowed(createModel.getJoinRequestsAllowed());
		space.setAllowRecommendation(createModel.getAllowRecommendation());
		space.setAllowLeave(createModel.getAllowLeave());
		space.setAutoWifiSyncAllowed(createModel.getAutoWifiSyncAllowed());
		space.setShowCommunity(createModel.getShowCommunity());
		space.setUser(user);

		if (createModel.getThumbnail() != null) {
			space.setThumbnail(createModel.getThumbnail());
		}

		space.setPrice(createModel.getPrice());
		if (createModel.getCreationDate() != null) {
			space.setCreationDate(DateConverter.convertZonedDateTimeToDate(createModel.getCreationDate()));
		}
		space = spaceRepository.save(space);

		var joined = new Joined();
		joined.setSpace(space);
		joined.setUser(user);
		joined.setSpaceRole(SpaceRole.OWNER);
		joinedRepository.save(joined);

		return ResponseModel.done(space.getId(),
				new SpaceInfoMessage(space.getId(), space.getName(), space.getThumbnail(),
						new From(user.getId(), user.getFullName(), user.getThumbnail(), user.getChatId()),
						null, null, space.getIsPrivate(), null));
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ')")
	public ResponseModel checkSpaceNameForUser(String spaceName) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						if (spaceRepository.countByNameAndUserIdAndDeletedFalse(spaceName, user.getId()) > 0) {
							throw new ExistException(spaceName);
						}
						return ResponseModel.done();
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ')")
	public PageResponseModel searchForSpace(String name, PageRequest pageRequestModel, String lang) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, PageResponseModel>() {
					@Override
					public PageResponseModel apply(User user) {
						Page<SpaceListingModel> spaceSearchModels = spaceRepository
								.searchForSpace(name, pageRequestModel)
								.map(new Function<Space, SpaceListingModel>() {
									@Override
									public SpaceListingModel apply(Space space) {
										var spaceListingModel = getSpaceListingModelForAdmins(space, null, lang);
										spaceListingModel.setRole(null);
										joinedRepository
												.findOneBySpaceIdAndUserIdAndDeletedFalse(space.getId(), user.getId())
												.ifPresent(new Consumer<Joined>() {
													@Override
													public void accept(Joined joined) {
														spaceListingModel.setJoinedStatus(joined.getJoinedStatus());
														spaceListingModel.setRole(joined.getSpaceRole());
													}
												});
										return spaceListingModel;
									}
								});
						return PageResponseModel.done(new ArrayList<>(spaceSearchModels.getContent()),
								spaceSearchModels.getTotalPages(),
								spaceSearchModels.getNumber(), spaceSearchModels.getTotalElements());
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.SPACE_DELETE)
	@PreAuthorize("hasAuthority('SPACE_DELETE')")
	@Message(entityAction = EntityAction.SPACE_DELETE, services = { Services.NOTIFICATIONS, Services.CHAT })
	public ResponseModel deleteSpace(Long id) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						var space = spaceRepository.findById(id).orElseThrow(NotFoundException::new);
						return deleteSpaceByObject(user, space);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	private ResponseModel deleteSpaceByObject(User user, Space space) {
		if (!Objects.equals(user, space.getUser())
				&& user.getType() != UserType.SYSTEM_ADMIN
				&& user.getType() != UserType.SUPER_ADMIN) {
			// In B2C, strict ownership or admin override
			throw new NotPermittedException();
		}

		List<Joined> joinedList = joinedRepository.findBySpaceInAndDeletedFalse(Collections.singleton(space))
				.collect(Collectors.toList());
		if (!joinedList.isEmpty()) {
			joinedRepository.deleteAll(joinedList);
		}
		List<Assessment> assessments = assessmentRepository.findBySpaceInAndDeletedFalse(Collections.singleton(space))
				.collect(Collectors.toList());
		if (!assessments.isEmpty()) {
			assessmentRepository.deleteAll(assessments);
		}

		List<Content> contents = contentRepository.findBySpaceInAndDeletedFalse(Collections.singleton(space))
				.collect(Collectors.toList());
		if (!contents.isEmpty()) {
			contentRepository.deleteAll(contents);
		}

		var discussions = discussionRepository
				.findBySpaceIdAndDeletedFalseOrderByCreationDateDesc(space.getId());
		if (!discussions.isEmpty()) {
			Set<String> parentIds = discussions.stream().map(Discussion::getId).collect(Collectors.toSet());
			var comments = commentRepository.findOneByParentIdInAndDeletedFalse(parentIds);
			if (!comments.isEmpty()) {
				commentRepository.deleteAll(comments);
			}
			discussionRepository.deleteAll(discussions);
		}
		spaceRepository.delete(space);
		return ResponseModel.done(null,
				new SpaceShareInfoMessage(space.getId(), space.getName(), space.getThumbnail(),
						new From(user.getId(), user.getFullName(), user.getThumbnail(), user.getChatId()),
						null, null, space.getIsPrivate(),
						joinedList.stream().map(new Function<Joined, Long>() {
							@Override
							public Long apply(Joined joined) {
								return joined.getUser().getId();
							}
						}).collect(Collectors.toSet()),
						space.getChatRoomId()));
	}

	@Transactional
	@Auditable(EntityAction.SPACE_SHARE)
	@PreAuthorize("hasAuthority('COMMUNITY_UPDATE')")
	@Message(entityAction = EntityAction.SPACE_SHARE, services = { Services.NOTIFICATIONS,
			Services.CHAT }, withModel = true)
	public ResponseModel shareSpaceToUsers(Long spaceId, SpaceShareModel spaceShareModel) {
		List<Joined> sharedWith = new ArrayList<>();
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user1) {
						if (null == spaceId) {
							throw new MintException(Code.INVALID_KEY);
						}
						var space = spaceRepository.findById(spaceId).orElseThrow(NotFoundException::new);

						checkUserForSpace(user1, space);

						Set<Joined> joineds = joinedRepository.findBySpaceIdAndDeletedFalse(spaceId)
								.collect(Collectors.toSet());

						if (spaceShareModel.getUsers().isEmpty()) {
							throw new MintException(Code.INVALID, "error.share.users");
						}
						if (!spaceShareModel.getUsers().isEmpty()) {
							userRepository.findAllById(
									spaceShareModel.getUsers().stream().map(SpaceRoleModel::getId)
											.collect(Collectors.toList()))
									.forEach(new Consumer<User>() {
										@Override
										public void accept(User user) {
											var spaceRoleModel = spaceShareModel.getUsers()
													.get(spaceShareModel.getUsers()
															.indexOf(new SpaceRoleModel(user.getId())));
											if (spaceRoleModel != null) {
												final Joined[] j = { new Joined(user, space) };
												joineds.stream().filter(new Predicate<Joined>() {
													@Override
													public boolean test(Joined j1) {
														return Objects.equals(j1, j[0]);
													}
												}).findFirst()
														.ifPresent(new Consumer<Joined>() {
															@Override
															public void accept(Joined joined) {
																j[0] = joined;
															}
														});
												if (!Objects.equals(j[0].getUser().getId(), user1.getId())
														&& j[0].getSpaceRole() != SpaceRole.OWNER) {
													j[0].setSpaceRole(spaceRoleModel.getRole());
												}
												if (!joineds.contains(j[0])) {
													joineds.add(j[0]);
													sharedWith.add(j[0]);
												}
											}
										}
									});
						}

						joinedRepository.saveAll(joineds);
						updateUserLastAccess(spaceId);
						var model = ResponseModel.done();
						model.setMessageData(new SpaceShareInfoMessage(space.getId(), space.getName(),
								space.getThumbnail(),
								new From(user1.getId(), user1.getFullName(), user1.getThumbnail(), user1.getChatId()),
								null, null, space.getIsPrivate(),
								sharedWith.stream().map(new Function<Joined, Long>() {
									@Override
									public Long apply(Joined joined) {
										return joined.getUser().getId();
									}
								}).collect(Collectors.toSet()),
								space.getChatRoomId()));
						return model;
					}

				}).orElseThrow(NotPermittedException::new);
	}

	private void checkUserForSpace(User user1, Space space) {
		if (user1.getType() == UserType.USER) {
			var joined = joinedRepository.findOneByUserIdAndSpaceIdAndDeletedFalse(user1.getId(), space.getId())
					.orElseThrow(NotPermittedException::new);
			if (joined.getSpaceRole() != SpaceRole.OWNER && joined.getSpaceRole() != SpaceRole.CO_OWNER) {
				throw new NotPermittedException();
			}
		}
	}

	@Transactional
	@Auditable(EntityAction.SPACE_JOIN_REQUEST)
	@PreAuthorize("hasAuthority('SPACE_JOINREQUEST_CREATE')")
	@Message(entityAction = EntityAction.SPACE_JOIN, services = { Services.NOTIFICATIONS, Services.CHAT })
	public ResponseModel joinSpace(Long spaceId) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						var space = spaceRepository.findById(spaceId).orElseThrow(NotFoundException::new);
						var joined = joinedValidation(user, space);

						joinedRepository.save(joined);
						updateSpaceModificationDate(space);
						var userInfoMessage = new UserInfoMessage(user);
						return ResponseModel.done(null,
								new SpaceJoinMessage(space.getId(), space.getName(), space.getThumbnail(),
										new From(userInfoMessage),
										null,
										null,
										space.getIsPrivate(), userInfoMessage, joined.getJoinedStatus(),
										space.getChatRoomId()));
					}
				}).orElseThrow(NotPermittedException::new);
	}

	private Joined joinedValidation(User user, Space space) {
		if (joinedRepository.findOneBySpaceIdAndUserIdAndDeletedFalse(space.getId(), user.getId()).isPresent()) {
			throw new MintException(Code.INVALID, "error.space.alreadyjoined");
		}
		var joined = new Joined(user, space);
		if (space.getIsPrivate().booleanValue() && space.getJoinRequestsAllowed()) {
			joined.setJoinedStatus(JoinedStatus.PENDING);
		} else if (space.getIsPrivate().booleanValue() && !space.getJoinRequestsAllowed()) {
			throw new NotPermittedException("error.space.join.private");
		}
		return joined;
	}

	@Transactional
	@Message(entityAction = EntityAction.SPACE_JOIN_ACCEPT, services = { Services.NOTIFICATIONS, Services.CHAT })
	public ResponseModel acceptJoinRequest(Long spaceId, Long userId) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						return spaceRepository.findOneByIdAndDeletedFalse(spaceId)
								.map(new Function<Space, ResponseModel>() {
									@Override
									public ResponseModel apply(Space space) {
										return joinedRepository
												.findOneBySpaceIdAndUserIdAndDeletedFalse(spaceId, userId)
												.map(new Function<Joined, ResponseModel>() {
													@Override
													public ResponseModel apply(Joined joined) {
														if (!space.getUser().equals(user)) {
															throw new NotPermittedException("error.space.join.approve");
														}

														joined.setJoinedStatus(JoinedStatus.JOINED);
														joinedRepository.save(joined);

														return ResponseModel.done(null, new SpaceJoinMessage(
																space.getId(), space.getName(),
																space.getThumbnail(),
																new From(new UserInfoMessage(user)),
																null,
																null,
																space.getIsPrivate(),
																new UserInfoMessage(joined.getUser()),
																JoinedStatus.JOINED,
																space.getChatRoomId()));
													}
												}).orElseThrow(new Supplier<InvalidException>() {
													@Override
													public InvalidException get() {
														return new InvalidException("error.space.join.user");
													}
												});
									}
								})
								.orElseThrow(NotFoundException::new);
					}
				})
				.orElseThrow(NotPermittedException::new);
	}

	@Transactional
	public ResponseModel refuseJoinRequest(Long spaceId, Long userId) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						return spaceRepository.findOneByIdAndDeletedFalse(spaceId)
								.map(new Function<Space, ResponseModel>() {
									@Override
									public ResponseModel apply(Space space) {
										return joinedRepository
												.findOneBySpaceIdAndUserIdAndDeletedFalse(spaceId, userId)
												.map(new Function<Joined, ResponseModel>() {
													@Override
													public ResponseModel apply(Joined joined) {
														if (!space.getUser().equals(user)) {
															throw new NotPermittedException("error.space.join.approve");
														}
														joined.setJoinedStatus(JoinedStatus.REFUSED);
														joinedRepository.save(joined);

														return ResponseModel.done(null, new SpaceJoinMessage(
																space.getId(), space.getName(),
																space.getThumbnail(),
																new From(new UserInfoMessage(user)),
																null,
																null,
																space.getIsPrivate(),
																new UserInfoMessage(joined.getUser()),
																JoinedStatus.JOINED,
																space.getChatRoomId()));
													}
												}).orElseThrow(new Supplier<InvalidException>() {
													@Override
													public InvalidException get() {
														return new InvalidException("error.space.join.user");
													}
												});
									}
								})
								.orElseThrow(NotFoundException::new);
					}
				})
				.orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.SPACE_LEAVE)
	@Message(entityAction = EntityAction.SPACE_LEAVE, services = Services.CHAT)
	public ResponseModel leaveSpace(Long spaceId) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						var space = spaceRepository.findById(spaceId).orElseThrow(NotFoundException::new);
						if (space.getAllowLeave() != null && !space.getAllowLeave()) {
							throw new MintException(Code.INVALID, "error.space.leavenotallowed");
						}
						joinedRepository.findOneBySpaceIdAndUserIdAndDeletedFalse(spaceId, user.getId())
								.ifPresent(new Consumer<Joined>() {
									@Override
									public void accept(Joined joined) {
										if (joined.getSpaceRole() == SpaceRole.OWNER) {
											throw new NotPermittedException();
										}
										joinedRepository.delete(joined);
										updateSpaceModificationDate(space);
									}
								});

						return ResponseModel.done(null, new SpaceInfoMessage(spaceId, null, null,
								new From(new UserInfoMessage(user)), null, null, null, space.getChatRoomId()));
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Async
	public void updateSpaceModificationDate(Space space) {
		synchronized (this) {
			spaceRepository.updateSpaceModificationDate(space);
		}
	}

	@Transactional
	public void updateUserLastAccess(Long spaceId) {
		var joined = joinedRepository
				.findOneBySpaceIdAndUserIdAndDeletedFalse(spaceId, SecurityUtils.getCurrentUser().getId())
				.orElseThrow(NotPermittedException::new);
		joined.setSpaceViewsCount(joined.getSpaceViewsCount() + 1);
		joined.setLastAccessed(new Date());
		joinedRepository.save(joined);
	}

	@Transactional
	@Auditable(EntityAction.SPACE_UNSHARE)
	@Message(entityAction = EntityAction.SPACE_UNSHARE, services = Services.CHAT)
	public ResponseModel unShareSpaceToUsers(Long spaceId, SpaceShareModel spaceShareModel) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user1) {
						List<Joined> unshared = new ArrayList<>();
						if (null == spaceId) {
							throw new MintException(Code.MISSING, "id");
						}

						var space = spaceRepository.findOneByIdAndDeletedFalse(spaceId)
								.orElseThrow(NotFoundException::new);

						checkUserForSpace(user1, space);
						if (!spaceShareModel.getUsers().isEmpty()) {
							List<Joined> joineds = joinedRepository.findBySpaceIdAndUserIdInAndDeletedFalse(spaceId,
									spaceShareModel.getUsers().stream().map(SpaceRoleModel::getId)
											.collect(Collectors.toList()))
									.collect(Collectors.toList());
							if (!joineds.isEmpty()) {
								unshared.addAll(joineds);
								joinedRepository.deleteAll(joineds);
							}
						}

						updateUserLastAccess(spaceId);
						var model = ResponseModel.done();
						model.setMessageData(
								new SpaceShareInfoMessage(space.getId(), null, null, null, null, null, null,
										unshared.stream().map(new Function<Joined, Long>() {
											@Override
											public Long apply(Joined joined) {
												return joined.getUser().getId();
											}
										}).collect(Collectors.toSet()),
										space.getChatRoomId()));
						return model;
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.SPACE_FAVORIT)
	public ResponseModel favoriteSpace(Long spaceId) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						if (null == spaceId) {
							throw new MintException(Code.INVALID_KEY);
						}
						return joinedRepository.findOneByUserIdAndSpaceIdAndDeletedFalse(user.getId(), spaceId)
								.map(new Function<Joined, ResponseModel>() {
									@Override
									public ResponseModel apply(Joined joined) {
										joined.setFavorite(Boolean.TRUE);
										joined.setLastAccessed(new Date());
										joinedRepository.save(joined);
										return ResponseModel.done();
									}
								}).orElseThrow(NotFoundException::new);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.SPACE_UNFAVORIT)
	public ResponseModel unFavoriteSpace(Long spaceId) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						if (null == spaceId) {
							throw new MintException(Code.INVALID_KEY);
						}
						return joinedRepository
								.findOneByUserIdAndSpaceIdAndFavoriteTrueAndDeletedFalse(user.getId(), spaceId)
								.map(new Function<Joined, ResponseModel>() {
									@Override
									public ResponseModel apply(Joined joined) {
										joined.setFavorite(Boolean.FALSE);
										joined.setLastAccessed(new Date());
										joinedRepository.save(joined);
										return ResponseModel.done();
									}
								}).orElseThrow(NotFoundException::new);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ')")
	public ResponseModel getFavoriteSpaces(Integer page, Integer size, SortField field, Sort.Direction direction,
			String lang) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, PageResponseModel>() {
					@Override
					public PageResponseModel apply(User user) {
						var pageRequest = getPageRequestForJoined(page, size, field, direction);
						var joinedPage = joinedRepository.getFavoriteSpaces(user.getId(), pageRequest);
						return PageResponseModel.done(
								joinedPage.getContent().stream().map(new Function<Joined, SpaceListingModel>() {
									@Override
									public SpaceListingModel apply(Joined joined) {
										return getUpdatesForSpaces(joined, null, lang);
									}
								})
										.collect(Collectors.toList()),
								joinedPage.getTotalPages(), pageRequest.getPageNumber(), joinedPage.getTotalElements());
					}
				}).orElseThrow(NotPermittedException::new);
	}

	private PageRequest getPageRequestForJoined(Integer page, Integer size, SortField field, Sort.Direction direction) {
		Sort sort = null;
		if (field != null && direction != null) {
			var fieldName = field.getFieldName();
			if (Objects.equals(fieldName, "name")) {
				fieldName = "space.name";
			}
			sort = Sort.by(direction, fieldName);
		}
		return PageRequestModel.getPageRequestModel(page, size, sort);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ')")
	public ResponseModel getRecentAccessedSpaces(String lang) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, PageResponseModel>() {
					@Override
					public PageResponseModel apply(User user) {
						var pageRequest = PageRequestModel.getPageRequestModel(0, 8);
						var joinedPage = joinedRepository.findByUserIdAndDeletedFalseOrderByLastAccessedDesc(
								user.getId(),
								pageRequest);
						return PageResponseModel.done(
								joinedPage.getContent().stream().map(new Function<Joined, SpaceListingModel>() {
									@Override
									public SpaceListingModel apply(Joined joined) {
										return getUpdatesForSpaces(joined, null, lang);
									}
								})
										.collect(Collectors.toList()),
								joinedPage.getTotalPages(), pageRequest.getPageNumber(), joinedPage.getTotalElements());
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ')")
	public ResponseModel getOwnedSpaces(Integer page, Integer size, SortField field, Sort.Direction direction,
			String lang) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, PageResponseModel>() {
					@Override
					public PageResponseModel apply(User user) {
						var pageRequest = getPageRequestForJoined(page, size, field, direction);
						var joinedPage = joinedRepository.getOwnedSpacesByUser(user.getId(), pageRequest);
						return PageResponseModel.done(
								joinedPage.getContent().stream().map(new Function<Joined, SpaceListingModel>() {
									@Override
									public SpaceListingModel apply(Joined joined) {
										return getUpdatesForSpaces(joined, null, lang);
									}
								})
										.collect(Collectors.toList()),
								joinedPage.getTotalPages(), pageRequest.getPageNumber(), joinedPage.getTotalElements());
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ')")
	public ResponseModel getAllSpaces(String lang, String name, Integer page, Integer size, SortField field,
			Sort.Direction direction) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, PageResponseModel>() {
					@Override
					public PageResponseModel apply(User user) {
						Sort sort = null;
						if (field != null && direction != null) {
							sort = Sort.by(direction, field.getFieldName());
						}

						var pageRequest = PageRequestModel.getPageRequestModel(page, size, sort);
						switch (user.getType()) {
							case SUPER_ADMIN:
							case SYSTEM_ADMIN:
								var systemAdminSpacePage = spaceRepository.findAll(pageRequest);
								return PageResponseModel.done(systemAdminSpacePage.getContent().stream()
										.map(new Function<Space, SpaceListingModel>() {
											@Override
											public SpaceListingModel apply(Space space) {
												return getSpaceListingModelForAdmins(space, null, lang);
											}
										}).collect(Collectors.toSet()),
										systemAdminSpacePage.getTotalPages(), pageRequest.getPageNumber(),
										systemAdminSpacePage.getTotalElements());

							default:
								pageRequest = getPageRequestForJoined(page, size, field, direction);
								Page<Joined> joinedPage = null;
								if (name == null || name.isEmpty()) {
									joinedPage = joinedRepository.findByUserIdAndDeletedFalse(user.getId(),
											pageRequest);
								} else {
									joinedPage = joinedRepository
											.findByUserIdAndSpaceNameIgnoreCaseContainingAndDeletedFalse(
													user.getId(), name, pageRequest);
								}

								return PageResponseModel.done(
										joinedPage.getContent().stream()
												.map(new Function<Joined, SpaceListingModel>() {
													@Override
													public SpaceListingModel apply(Joined joined) {
														return getUpdatesForSpaces(joined, joined.getLastAccessed(),
																lang);
													}
												})
												.collect(Collectors.toList()),
										joinedPage.getTotalPages(), pageRequest.getPageNumber(),
										joinedPage.getTotalElements());
						}
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ') AND hasAuthority('SYSTEM_ADMIN')")
	public ResponseModel getCloudSpace(PageRequest pageRequest) {
		var adminSpacePage = spaceRepository.findByDeletedFalse(pageRequest);
		return PageResponseModel.done(
				adminSpacePage.getContent().stream().map(new Function<Space, SpaceListingModel>() {
					@Override
					public SpaceListingModel apply(Space space) {
						return getSpaceListingModelForAdmins(space, null, "ens");
					}
				})
						.collect(Collectors.toSet()),
				adminSpacePage.getTotalPages(), pageRequest.getPageNumber(), adminSpacePage.getContent().size());
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ')")
	public ResponseModel getSpaceUpdates(ZonedDateTime lastRequestDate) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						log.debug("date ==> {}", lastRequestDate);
						var updateModel = new SpaceListingUpdateModel();

						var date = DateConverter.convertZonedDateTimeToDate(lastRequestDate);

						final var queryDate = date;
						var calendar = Calendar.getInstance();
						calendar.setTime(date);
						calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
						calendar.set(Calendar.MILLISECOND, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MINUTE, 0);
						date = calendar.getTime();

						log.debug("date after remove ==> {}", date);
						updateModel.getNewSpaces()
								.addAll(spaceRepository
										.findByUserIdAndDeletedFalseAndCreationDateAfter(user.getId(), date)
										.map(new Function<Space, SpaceListingModel>() {
											@Override
											public SpaceListingModel apply(Space space) {
												return getSpaceListingModelForAdmins(space, queryDate, "en");
											}
										})
										.collect(Collectors.toList()));

						updateModel.getJoinedSpaces()
								.addAll(joinedRepository
										.findByUserIdAndCreationDateAfterAndDeletedFalse(user.getId(), date)
										.map(new Function<Joined, SpaceListingModel>() {
											@Override
											public SpaceListingModel apply(Joined joined) {
												return getUpdatesForSpaces(joined, queryDate, "en");
											}
										}).collect(Collectors.toList()));

						updateModel.getUpdatesSpaces().addAll(joinedRepository
								.findByDeletedFalseAndUserIdAndSpaceDeletedFalseAndSpaceLastModifiedDateNotNullAndSpaceLastModifiedDateAfter(
										user.getId(), date)
								.map(new Function<Joined, SpaceListingModel>() {
									@Override
									public SpaceListingModel apply(Joined joined) {
										return getUpdatesForSpaces(joined, queryDate, "en");
									}
								}).collect(Collectors.toList()));
						updateModel.getUpdatesSpaces()
								.addAll(joinedRepository
										.findByUserIdAndLastModifiedDateAfterAndDeletedFalse(user.getId(), date)
										.map(new Function<Joined, SpaceListingModel>() {
											@Override
											public SpaceListingModel apply(Joined joined) {
												return getUpdatesForSpaces(joined, queryDate, "en");
											}
										}).collect(Collectors.toList()));
						updateModel.getUnSharedSpaces()
								.addAll(joinedRepository
										.findByUserIdAndDeletedTrueAndDeletedDateAfterAndSpaceDeletedFalse(user.getId(),
												date)
										.map(new Function<Joined, Long>() {
											@Override
											public Long apply(Joined joined) {
												return joined.getSpace().getId();
											}
										}).collect(Collectors.toList()));

						updateModel.getDeletedSpaces()
								.addAll(joinedRepository
										.findByUserIdAndDeletedTrueAndSpaceDeletedTrueAndSpaceDeletedDateGreaterThanEqual(
												user.getId(), date)
										.map(new Function<Joined, Long>() {
											@Override
											public Long apply(Joined joined) {
												return joined.getSpace().getId();
											}
										}).collect(Collectors.toList()));

						return ResponseModel.done(updateModel);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.SPACE_RATE)
	@Message(entityAction = EntityAction.SPACE_RATE, services = Services.NOTIFICATIONS)
	public ResponseModel rateSpace(Long spaceId, Integer rating) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						if (null == spaceId) {
							throw new MintException(Code.INVALID_KEY);
						}
						return joinedRepository.findOneByUserIdAndSpaceIdAndDeletedFalse(user.getId(), spaceId)
								.map(new Function<Joined, ResponseModel>() {
									@Override
									public ResponseModel apply(Joined joined) {
										joined.setRating(rating);
										joined.setLastAccessed(new Date());
										joinedRepository.save(joined);
										var avgRating = joinedRepository.getAvarageRatingOnSpace(spaceId);
										var space = joined.getSpace();
										space.setRating(avgRating);
										spaceRepository.save(space);
										return ResponseModel.done(space.getRating(),
												new SpaceInfoMessage(spaceId, space.getName(), space.getImage(),
														new From(SecurityUtils.getCurrentUser()), null,
														null, space.getIsPrivate(),
														space.getChatRoomId()));
									}
								}).orElseThrow(NotFoundException::new);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.SPACE_UPDATE)
	@PreAuthorize("hasAuthority('SPACE_UPDATE')")
	@Message(entityAction = EntityAction.SPACE_UPDATE, services = { Services.NOTIFICATIONS, Services.CHAT })
	public ResponseModel updateSpace(Long spaceId, SpaceCreateModel spaceCreateModel) {
		var currentUser = userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.orElseThrow(NotPermittedException::new);

		final var space = spaceRepository.findById(spaceId)
				.orElseThrow(new Supplier<NotFoundException>() {
					@Override
					public NotFoundException get() {
						return new NotFoundException("error.space.notfound");
					}
				});
		if (!space.getUser().equals(currentUser)) {
			// In B2C, only owner can update (or maybe admin, but removing B2B hierarchy
			// checks)
			throw new NotPermittedException();
		}

		// Category update removed

		final User[] user = { space.getUser() };
		if (spaceCreateModel.getOwnerId() != null
				&& !Objects.equals(spaceCreateModel.getOwnerId(), space.getUser().getId())) {
			userRepository.findOneByIdAndDeletedFalse(spaceCreateModel.getOwnerId()).ifPresent(new Consumer<User>() {
				@Override
				public void accept(User user1) {
					user[0] = user1;
				}
			});
		} else {
			user[0] = space.getUser();
		}

		if (!Objects.equals(space.getName(), spaceCreateModel.getName())
				&& spaceRepository.countByNameAndUserIdAndDeletedFalse(spaceCreateModel.getName(),
						user[0].getId()) > 0) {
			throw new ExistException(spaceCreateModel.getName());
		}
		var oldUser = space.getUser();
		space.setName(spaceCreateModel.getName());
		if (spaceCreateModel.getTags() != null) {
			space.setObjective(Arrays.toString(spaceCreateModel.getTags().toArray()));
		}
		space.setPrice(spaceCreateModel.getPrice());
		space.setPaid(spaceCreateModel.getPaid());
		space.setIsPrivate(spaceCreateModel.getIsPrivate());

		if (spaceCreateModel.getImage() != null) {
			space.setImage(spaceCreateModel.getImage());
		}
		if (spaceCreateModel.getThumbnail() != null) {
			space.setThumbnail(spaceCreateModel.getThumbnail());
		}

		space.setDescription(spaceCreateModel.getDescription());
		space.setColor(spaceCreateModel.getColor());
		space.setJoinRequestsAllowed(spaceCreateModel.getJoinRequestsAllowed());
		space.setAutoWifiSyncAllowed(spaceCreateModel.getAutoWifiSyncAllowed());
		space.setAllowRecommendation(spaceCreateModel.getAllowRecommendation());
		space.setAllowLeave(spaceCreateModel.getAllowLeave());
		space.setShowCommunity(spaceCreateModel.getShowCommunity());

		space.setPrice(spaceCreateModel.getPrice());
		if (!Objects.equals(user[0], oldUser)) {
			space.setUser(user[0]);
			joinedRepository.findOneByUserIdAndSpaceIdAndDeletedFalse(user[0].getId(), spaceId)
					.ifPresent(joinedRepository::delete);
			joinedRepository.findOneByUserIdAndSpaceIdAndDeletedFalse(oldUser.getId(), spaceId)
					.ifPresent(joinedRepository::delete);
			var joined = new Joined(user[0], space);
			joined.setSpaceRole(SpaceRole.OWNER);
			joinedRepository.save(joined);
		}
		spaceRepository.save(space);
		var spaceListingModel = getSpaceListingModelForAdmins(space, null, "en");
		return ResponseModel
				.done(spaceListingModel,
						new SpaceInfoMessage(space.getId(), space.getName(), space.getThumbnail(),
								new From(user[0].getId(), user[0].getFullName(), user[0].getThumbnail(),
										user[0].getChatId()),
								null, null, space.getIsPrivate(),
								space.getChatRoomId()));
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('SPACE_READ')")
	public ResponseModel getSpaceById(Long id, String lang) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						if (user.getType() != UserType.USER) {
							return spaceRepository.findById(id)
									.map(new Function<Space, ResponseModel>() {
										@Override
										public ResponseModel apply(Space space) {
											return ResponseModel.done(getSpaceListingModelForAdmins(space, null, lang));
										}
									})
									.orElseThrow(NotFoundException::new);
						}
						return joinedRepository.findOneByUserIdAndSpaceIdAndDeletedFalse(user.getId(), id)
								.map(new Function<Joined, ResponseModel>() {
									@Override
									public ResponseModel apply(Joined joined) {
										return ResponseModel.done(getUpdatesForSpaces(joined, null, lang));
									}
								})
								.orElseThrow(NotFoundException::new);
					}

				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseModel getUserBySpaceID(Long id) {
		return ResponseModel.done(joinedRepository.getSpaceCommunity(id)
				.filter(new Predicate<Joined>() {
					@Override
					public boolean test(Joined joined) {
						return !Objects.equals(joined.getUser().getUserName(), SecurityUtils.getCurrentUserLogin());
					}
				})
				.map(new Function<Joined, UserInfoModel>() {
					@Override
					public UserInfoModel apply(Joined joined) {
						return new UserInfoModel(joined.getUser());
					}
				}).collect(Collectors.toList()));
	}

	@Transactional
	@Auditable(EntityAction.SPACE_UPDATE)
	@PreAuthorize("hasAuthority('COMMUNITY_UPDATE')")
	public ResponseModel changeShareRole(Long spaceId, Long userId, SpaceRole spaceRole) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user1) {
						var currentUserJoined = joinedRepository
								.findOneBySpaceIdAndUserIdAndDeletedFalse(spaceId, user1.getId())
								.orElseThrow(NotPermittedException::new);
						if (currentUserJoined.getSpaceRole() != SpaceRole.OWNER
								&& currentUserJoined.getSpaceRole() != SpaceRole.CO_OWNER) {
							throw new NotPermittedException();
						}
						return joinedRepository.findOneBySpaceIdAndUserIdAndDeletedFalse(spaceId, userId)
								.map(new Function<Joined, ResponseModel>() {
									@Override
									public ResponseModel apply(Joined joined) {
										joined.setSpaceRole(spaceRole);
										joinedRepository.save(joined);
										return ResponseModel.done();
									}
								}).orElseThrow(NotFoundException::new);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.SPACE_CREATE)
	@PreAuthorize("hasAuthority('SPACE_CREATE')")
	public ResponseModel duplicateSpace(Long spaceId) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						return spaceRepository.findById(spaceId).map(new Function<Space, ResponseModel>() {
							@Override
							public ResponseModel apply(Space space) {
								var sizeTrim = space.getName().length() <= 44 ? space.getName().length() : 44;
								var spaceDuplicate = new Space("(copy) " + space.getName().substring(0, sizeTrim),
										space.getObjective(), space.getPrice(), space.getPaid(), space.getIsPrivate(),
										space.getImage(), space.getDescription());
								spaceDuplicate
										.setColor(space.getColor() == null ? RandomUtils.genertateRandomColor()
												: space.getColor());
								spaceDuplicate.setJoinRequestsAllowed(space.getJoinRequestsAllowed());
								spaceDuplicate.setAutoWifiSyncAllowed(space.getAutoWifiSyncAllowed());
								spaceDuplicate.setAllowRecommendation(space.getAllowRecommendation());
								spaceDuplicate.setShowCommunity(space.getShowCommunity());
								spaceDuplicate.setUser(user);
								spaceDuplicate.setThumbnail(space.getThumbnail());
								spaceDuplicate.setPrice(space.getPrice());
								spaceDuplicate.setImage(space.getImage());
								space.getContents().forEach(new Consumer<Content>() {
									@Override
									public void accept(Content content) {
										if (content.isDeleted()) {
											return;
										}
										var contentDup = new Content();
										contentDup.setName(content.getName());
										contentDup.setCheckSum(content.getCheckSum());
										contentDup.setExt(content.getExt());
										contentDup.setFolderName(content.getFolderName());
										contentDup.setFileName(content.getFileName());
										contentDup.setOwner(content.getOwner());
										contentDup.setShelfName(content.getShelfName());
										contentDup.setSize(content.getSize());
										contentDup.setSpace(spaceDuplicate);
										contentDup.setStatus(content.getStatus());
										contentDup.setTags(content.getTags());
										contentDup.setThumbnail(content.getThumbnail());
										contentDup.setType(content.getType());
										contentDup.setKeyId(content.getKeyId());
										contentDup.setKey(content.getKey());
										spaceDuplicate.getContents().add(contentDup);
									}
								});
								spaceRepository.save(spaceDuplicate);
								var joined = new Joined(user, spaceDuplicate);
								joined.setSpaceRole(SpaceRole.OWNER);
								joinedRepository.save(joined);

								return ResponseModel.done(spaceDuplicate.getId());
							}
						}).orElseThrow(NotFoundException::new);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	public void getCommunity(Space space, SpaceListingModel spaceListingModel) {
		List<Joined> joineds = joinedRepository.findBySpaceIdAndDeletedFalse(space.getId())
				.collect(Collectors.toList());
		spaceListingModel.setCommunitySize(joineds.size() > 0 ? joineds.size() - 1 : 0);
		joineds.stream().limit(4).forEach(new Consumer<Joined>() {
			@Override
			public void accept(Joined joined1) {
				var spaceUserModel = SpaceMapper.INSTANCE.userToSpaceUserModel(joined1.getUser());
				spaceUserModel.setSpaceRole(joined1.getSpaceRole());
				spaceListingModel.getCommunity().add(spaceUserModel);
			}
		});
	}

	public SpaceListingModel getUpdatesForSpaces(Joined joined, Date lastDate, String lang) {
		var spaceListingModel = getSpaceListingModelForAdmins(joined.getSpace(), lastDate, lang);
		spaceListingModel.setJoinedStatus(joined.getJoinedStatus());
		spaceListingModel.setFavorite(joined.getFavorite() != null && joined.getFavorite());
		if (spaceListingModel.isOwner() && joined.getSpaceRole() != SpaceRole.OWNER) {
			spaceListingModel.getPermissions().putAll(SpaceRole.OWNER.getPermissions());
			spaceListingModel.setRole(SpaceRole.OWNER);
		} else {
			spaceListingModel.setRole(joined.getSpaceRole() == null ? SpaceRole.VIEWER : joined.getSpaceRole());
			spaceListingModel.getPermissions().putAll(spaceListingModel.getRole().getPermissions());
		}

		return spaceListingModel;
	}

	private void extractTags(Space space, SpaceListingModel spaceListingModel) {
		if (space.getObjective() != null) {
			if (space.getObjective().startsWith("[")) {
				var tags = space.getObjective().replace("[", "").replace("]", "");
				spaceListingModel.setTags(Arrays.asList(tags.split(",")));

			} else {
				spaceListingModel.setTags(Collections.singletonList(space.getObjective()));
			}
		}
	}

	private SpaceListingModel getSpaceListingModelForAdmins(Space space, Date lastDate, String lang) {

		var spaceListingModel = new SpaceListingModel(url);
		SpaceMapper.INSTANCE.mapSpaceDomainToListingModel(space, spaceListingModel);
		spaceListingModel.setRole(SpaceRole.OWNER);
		spaceListingModel.setOwner(space.getUser().getUserName().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin()));
		spaceListingModel.setCreationDate(DateConverter.convertDateToZonedDateTime(space.getCreationDate()));
		spaceListingModel.setLastModified(DateConverter.convertDateToZonedDateTime(space.getLastModifiedDate()));
		getCommunity(space, spaceListingModel);
		extractTags(space, spaceListingModel);
		spaceListingModel.setContentSize(contentRepository.countBySpace(space));
		if (spaceListingModel.isOwner()) {
			spaceListingModel.getPermissions().putAll(SpaceRole.OWNER.getPermissions());
		}

		if (!spaceListingModel.getImage().startsWith(url) && !spaceListingModel.getImage().startsWith("http://")
				&& !spaceListingModel.getImage().startsWith("//")) {
			spaceListingModel.setImage(String.format("%s%s", url, spaceListingModel.getImage()));
		}
		if (!spaceListingModel.getThumbnail().startsWith(url) && !spaceListingModel.getThumbnail().startsWith("http://")
				&& !spaceListingModel.getThumbnail().startsWith("//")) {
			spaceListingModel.setThumbnail(String.format("%s%s", url, spaceListingModel.getThumbnail()));
		}
		if (lastDate != null) {
			spaceListingModel.setNewContent(
					contentRepository.countBySpaceIdAndDeletedFalseAndCreationDateAfter(space.getId(), lastDate) > 0);
			// Todo: Flag Assessment
			// Todo: Flag comments
		}

		return spaceListingModel;
	}

	@Transactional
	public ResponseModel joinWithTags(List<String> tags, boolean closeAutoLogin) {
		log.debug("start auto join ");
		var user = userRepository.findById(SecurityUtils.getCurrentUser().getId())
				.orElseThrow(NotPermittedException::new);

		if (tags != null && !tags.isEmpty()) {
			Set<Joined> joineds = new HashSet<>();
			for (String string : tags) {
				joineds.addAll(spaceRepository.findByIsPrivateFalseAndObjectiveContains(string)
						.map(new Function<Space, Joined>() {
							@Override
							public Joined apply(Space space) {
								try {
									return joinedValidation(user, space);
								} catch (MintException e) {
									return null;
								}
							}
						}).filter(Objects::nonNull).collect(Collectors.toSet()));
			}

			log.debug("joined size {} ", joineds.size());
			if (joineds != null && !joineds.isEmpty()) {
				log.debug("join done)");
				joinedRepository.saveAll(joineds);
			}
		}
		log.debug("update flag with {}", closeAutoLogin);
		user.setAutoJoin(closeAutoLogin);
		userRepository.save(user);
		return ResponseModel.done();
	}
}
