package com.edumento.notification.handlers.impl;

import static com.edumento.core.constants.notification.Actions.ACCEPTED;
import static com.edumento.core.constants.notification.Actions.CREATE;
import static com.edumento.core.constants.notification.Actions.DELETE;
import static com.edumento.core.constants.notification.Actions.JOIN;
import static com.edumento.core.constants.notification.Actions.RATE;
import static com.edumento.core.constants.notification.Actions.SHARED;
import static com.edumento.core.constants.notification.Actions.UPDATE;
import static com.edumento.core.constants.notification.EntityType.SPACE;
import static com.edumento.core.constants.notification.MessageCategory.USER;

import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.edumento.core.constants.SpaceRole;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.Target;
import com.edumento.core.model.messages.space.SpaceInfoMessage;
import com.edumento.core.model.messages.space.SpaceJoinMessage;
import com.edumento.core.model.messages.space.SpaceShareInfoMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.service.MailService;
import com.edumento.notification.util.Utilities;
import com.edumento.space.domain.Joined;
import com.edumento.user.domain.User;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
@Component
public class SpaceNotificationHandler extends AbstractHandler {

	private final Utilities utilities;

	@Value("${enable-edit-space-email:false}")
	private boolean enableEditSpaceEmail;

	@Value("${enable-rate-space-email:false}")
	private boolean enableRateSpaceEmail;

	@Autowired
	public SpaceNotificationHandler(UserRepository userRepository, AmqNotifier amqNotifier, MailService mailService,
			ObjectMapper objectMapper, Utilities utilities) {
		super(userRepository, amqNotifier, mailService, objectMapper);
		this.utilities = utilities;
	}

	@Override
	protected void handleNonCRUDAction(BaseMessage notificationMessage) {
		logger.debug("handle Non crud action");
		logger.debug(notificationMessage.toString());
		switch (notificationMessage.getEntityAction().getAction()) {
		case RATE:
			logger.debug("rate space");
			onRate(notificationMessage);
			break;

		case SHARED:
			logger.debug(" share message");
			onShare(notificationMessage);
			break;
		case JOIN:
			logger.debug("join space");
			onJoin(notificationMessage);
			break;
		case ACCEPTED:
			logger.debug("accepted");
			onAcceptJoin(notificationMessage);
			break;
		default:
			logger.debug(notificationMessage.getEntityAction().getAction().toString());
			break;
		}
	}

	private void onAcceptJoin(BaseMessage baseMessage) {
		var spaceJoinMessage = mapJsonObject(baseMessage, SpaceJoinMessage.class);
		var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), USER,
				spaceJoinMessage.getFrom(),
				new Target(SPACE, spaceJoinMessage.getId().toString(), JOIN, spaceJoinMessage.getImage()));
		var user = userRepository.findById(spaceJoinMessage.getJoinedInfoMessage().getId()).orElse(null);
		if (user != null) {
			var userInfoMessage = new UserInfoMessage(user);
			var notificationMessage = amqNotifier.saveMessage(userInfoMessage, baseNotificationMessage,
					createMessage(baseMessage), null, spaceJoinMessage.getName(),
					getCantegoryNameByLang(user.getLangKey(), spaceJoinMessage.getCategoryName(),
							spaceJoinMessage.getCategoryNameAR()));
			if (user.getNotification() && !user.isDeleted()) {
				amqNotifier.send(notificationMessage);
				if (user.getMailNotification()) {
					mailService.sendNotificationMail(notificationMessage, userInfoMessage, true, false);
				}
			}
		}
	}

	private void onJoin(BaseMessage baseMessage) {
		var spaceJoinMessage = mapJsonObject(baseMessage, SpaceJoinMessage.class);
		logger.debug(spaceJoinMessage.toString());
		var message = "mint.notification.message.space.joined";
		if (spaceJoinMessage.getIsPrivate() != null && spaceJoinMessage.getIsPrivate().booleanValue()) {
			message = "mint.notification.message.space.join";
		}
		var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), USER,
				spaceJoinMessage.getFrom(),
				new Target(SPACE, spaceJoinMessage.getId().toString(), JOIN, spaceJoinMessage.getImage()));

		var user = utilities.getSpaceOwner(spaceJoinMessage.getId());
		logger.debug(user.toString());
		if (user != null) {
			var userInfoMessage = new UserInfoMessage(user);
			var notificationMessage = amqNotifier.saveMessage(userInfoMessage, baseNotificationMessage,
					message, null, spaceJoinMessage.getName(), getCantegoryNameByLang(user.getLangKey(),
							spaceJoinMessage.getCategoryName(), spaceJoinMessage.getCategoryNameAR()));
			logger.debug(user.toString());
			if (user.getNotification() && !user.isDeleted()) {
				amqNotifier.send(notificationMessage);
				if (user.getMailNotification()) {
					logger.debug("has notification flag" + user.getMailNotification().toString());
				}
				mailService.sendNotificationMail(notificationMessage, userInfoMessage, true, false);
			}
		}
	}

	@Override
	protected void onCreate(BaseMessage baseMessage) {
		var spaceInfoMessage = mapJsonObject(baseMessage, SpaceInfoMessage.class);
		if (spaceInfoMessage.getIsPrivate().booleanValue()) {
			return;
		}

		var followers = utilities.getFollowerList(spaceInfoMessage.getFrom().getId());

		var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), USER,
				spaceInfoMessage.getFrom(),
				new Target(SPACE, spaceInfoMessage.getId().toString(), CREATE, spaceInfoMessage.getImage()));

		if (null != followers && !followers.isEmpty()) {
			followers.forEach(new Consumer<User>() {
				@Override
				public void accept(User user) {
					var userInfoMessage = new UserInfoMessage(user);
					var notificationMessage = amqNotifier.saveMessage(userInfoMessage,
							baseNotificationMessage, createMessage(baseMessage), null, spaceInfoMessage.getName(),
							getCantegoryNameByLang(user.getLangKey(), spaceInfoMessage.getCategoryName(),
									spaceInfoMessage.getCategoryNameAR()));
					if (user.getNotification() && !user.isDeleted()) {
						amqNotifier.send(notificationMessage);
						if (user.getMailNotification()) {
							mailService.sendNotificationMail(notificationMessage, userInfoMessage, true, false);
						}
					}
				}
			});
		}
	}

	@Override
	protected void onUpdate(BaseMessage baseMessage) {
		var spaceInfoMessage = mapJsonObject(baseMessage, SpaceInfoMessage.class);

		var community = utilities.getCommunityUserList(spaceInfoMessage.getId(),
				spaceInfoMessage.getFrom().getId());

		if (null != community && !community.isEmpty()) {
			community.stream().forEach(new Consumer<Joined>() {
				@Override
				public void accept(Joined joined) {
					var userInfoMessage = new UserInfoMessage(joined.getUser());
					var notificationMessage = amqNotifier.saveMessage(userInfoMessage,
							new BaseNotificationMessage(ZonedDateTime.now(), USER, spaceInfoMessage.getFrom(),
									new Target(SPACE, spaceInfoMessage.getId().toString(), UPDATE,
											spaceInfoMessage.getImage())),
							createMessage(baseMessage), null, spaceInfoMessage.getName(),
							getCantegoryNameByLang(joined.getUser().getLangKey(), spaceInfoMessage.getCategoryName(),
									spaceInfoMessage.getCategoryNameAR()));

					if (joined.getNotification() && joined.getUser().getNotification() && !joined.getUser().isDeleted()) {
						amqNotifier.send(notificationMessage);
						if (enableEditSpaceEmail && joined.getUser().getMailNotification()) {
							logger.debug("has notification flag" + joined.getUser().getMailNotification().toString());
						}
						mailService.sendNotificationMail(notificationMessage, userInfoMessage, true, true);
					}
				}
			});
		}
	}

	@Override
	protected void onDelete(BaseMessage baseMessage) {
		var spaceInfoMessage = mapJsonObject(baseMessage, SpaceShareInfoMessage.class);
		if (spaceInfoMessage.getUserIds() != null && !spaceInfoMessage.getUserIds().isEmpty()) {
			var community = userRepository.findAllById(spaceInfoMessage.getUserIds());
			var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), USER,
					spaceInfoMessage.getFrom(), new Target(SPACE, spaceInfoMessage.getId().toString(), DELETE));

			if (null != community) {
				community.forEach(new Consumer<User>() {
					@Override
					public void accept(User user) {
						var userInfoMessage = new UserInfoMessage(user);
						var notificationMessage1 = amqNotifier.saveMessage(userInfoMessage,
								baseNotificationMessage, createMessage(baseMessage), null, spaceInfoMessage.getName(),
								getCantegoryNameByLang(user.getLangKey(), spaceInfoMessage.getCategoryName(),
										spaceInfoMessage.getCategoryNameAR()));
						if (user.getNotification() && !user.isDeleted()) {
							amqNotifier.send(notificationMessage1);
							if (user.getMailNotification()) {
								mailService.sendNotificationMail(notificationMessage1, userInfoMessage, true, false);
							}
						}
					}
				});
			}
		}
	}

	private void onRate(BaseMessage baseMessage) {
		var spaceInfoMessage = mapJsonObject(baseMessage, SpaceInfoMessage.class);
		logger.debug(spaceInfoMessage.toString());
		var community = utilities.getCommunityUserList(spaceInfoMessage.getId(),
				spaceInfoMessage.getFrom().getId());
		logger.debug(community.toString());
		var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), USER,
				spaceInfoMessage.getFrom(),
				new Target(SPACE, spaceInfoMessage.getId().toString(), UPDATE, spaceInfoMessage.getImage()));

		if (null != community && !community.isEmpty()) {
			community.stream().filter(new Predicate<Joined>() {
				@Override
				public boolean test(Joined joined) {
					return joined.getSpaceRole() == SpaceRole.OWNER;
				}
			}).forEach(new Consumer<Joined>() {
				@Override
				public void accept(Joined joined) {
					var userInfoMessage = new UserInfoMessage(joined.getUser());
					var notificationMessage = amqNotifier.saveMessage(userInfoMessage,
							baseNotificationMessage, createMessage(baseMessage), null, spaceInfoMessage.getName(),
							getCantegoryNameByLang(joined.getUser().getLangKey(), spaceInfoMessage.getCategoryName(),
									spaceInfoMessage.getCategoryNameAR()));
					logger.debug(joined.toString());
					if (joined.getNotification() && joined.getUser().getNotification() && !joined.getUser().isDeleted()) {
						amqNotifier.send(notificationMessage);
						logger.debug("has notification flag" + joined.getUser().getMailNotification().toString());
						if (enableRateSpaceEmail && joined.getUser().getMailNotification()) {
							mailService.sendNotificationMail(notificationMessage, userInfoMessage, true, true);
						}
					}
				}
			});
		}
	}

	private void onShare(BaseMessage baseMessage) {
		var spaceShareInfoMessage = mapJsonObject(baseMessage, SpaceShareInfoMessage.class);

		var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), USER,
				spaceShareInfoMessage.getFrom(), new Target(SPACE, spaceShareInfoMessage.getId().toString(), SHARED));
		userRepository.findByIdInAndDeletedFalse(spaceShareInfoMessage.getUserIds()).forEach(new Consumer<User>() {
			@Override
			public void accept(User user) {
				var infoMessage = new UserInfoMessage(user);
				var notificationMessage1 = amqNotifier.saveMessage(infoMessage, baseNotificationMessage,
						createMessage(baseMessage), null, spaceShareInfoMessage.getName(),
						getCantegoryNameByLang(user.getLangKey(), spaceShareInfoMessage.getCategoryName(),
								spaceShareInfoMessage.getCategoryNameAR()));
				if (user.getNotification()) {
					amqNotifier.send(notificationMessage1);
					if (user.getMailNotification()) {
						mailService.sendNotificationMail(notificationMessage1, infoMessage, true, false);
					}
				}
			}
		});
	}

	private String getCantegoryNameByLang(String lang, String categoryname, String categorynameAR) {
		if ("ar".equals(lang) && categorynameAR != null && !"".equals(categorynameAR)) {
			return categorynameAR;
		} else {
			return categoryname;
		}
	}
}
