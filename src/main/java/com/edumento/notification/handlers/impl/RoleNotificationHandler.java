package com.edumento.notification.handlers.impl;

import static com.edumento.core.constants.notification.Actions.ASSIGN;
import static com.edumento.core.constants.notification.Actions.UNASSIGN;
import static com.edumento.core.constants.notification.Actions.UPDATE;
import static com.edumento.core.constants.notification.EntityType.ROLE;
import static com.edumento.core.constants.notification.MessageCategory.APP;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.b2b.domain.Role;
import com.edumento.b2b.repo.RoleRepository;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.Target;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.models.UserRoleModel;
import com.edumento.notification.service.MailService;
import com.edumento.user.domain.User;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */

//TODO: TetFawar
@Component
public class RoleNotificationHandler extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(RoleNotificationHandler.class);
	private final RoleRepository roleRepository;

	public RoleNotificationHandler(UserRepository userRepository, AmqNotifier amqNotifier, MailService mailService,
			ObjectMapper objectMapper, RoleRepository roleRepository) {
		super(userRepository, amqNotifier, mailService, objectMapper);
		this.roleRepository = roleRepository;
	}

	@Transactional
	@Override
	protected void handleNonCRUDAction(BaseMessage notificationMessage) {
		switch (notificationMessage.getEntityAction().getAction()) {
		case ASSIGN:
			onAssign(notificationMessage);
			break;
		case UNASSIGN:
			onUnAssign(notificationMessage);
			break;
		}
	}

	private void onUnAssign(BaseMessage notificationMessage) {
		try {
			var userList = objectMapper.readValue(notificationMessage.getDataModel(), Long[].class);
			long roleId = (Integer) notificationMessage.getEntityId();
			userRepository.findOneByUserNameAndDeletedFalse(notificationMessage.getUserName()).ifPresent(new Consumer<User>() {
				@Override
				public void accept(User user) {
					userRepository.findByIdInAndDeletedFalse(Arrays.asList(userList)).forEach(new Consumer<User>() {
						@Override
						public void accept(User user1) {
							var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(),
									APP, new From(user.getId(), user.getUserName()),
									new Target(ROLE, Long.toString(roleId), UNASSIGN));
							amqNotifier.send(amqNotifier.saveMessage(user1, baseNotificationMessage));
						}
					});
				}
			});
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private void onAssign(BaseMessage notificationMessage) {
		try {
			var userList = objectMapper.readValue(notificationMessage.getDataModel(), Long[].class);
			var roleId = Long.valueOf((Integer) notificationMessage.getEntityId());
			if (userList != null) {
				userRepository.findOneByUserNameAndDeletedFalse(notificationMessage.getUserName()).ifPresent(new Consumer<User>() {
					@Override
					public void accept(User user) {
						roleRepository.findOneByIdAndDeletedFalse(roleId).ifPresent(new Consumer<Role>() {
							@Override
							public void accept(Role role) {
								userRepository.findByIdInAndDeletedFalse(Arrays.asList(userList)).forEach(new Consumer<User>() {
									@Override
									public void accept(User user1) {
										var baseNotificationMessage = new BaseNotificationMessage(
												ZonedDateTime.now(), APP, new From(user.getId(), user.getUserName()),
												new Target(ROLE, roleId.toString(), UPDATE));
										amqNotifier.send(amqNotifier.saveMessage(user1, baseNotificationMessage));
									}
								});
							}
						});
					}
				});
			}
		} catch (IOException e) {
			try {
				var userRoleModel = objectMapper.readValue(notificationMessage.getDataModel(),
						UserRoleModel.class);
				if (userRoleModel.getRoleId() != null && userRoleModel.getUserId() != null) {
					userRepository.findOneByUserNameAndDeletedFalse(notificationMessage.getUserName())
							.ifPresent(new Consumer<User>() {
								@Override
								public void accept(User user) {
									roleRepository.findOneByIdAndDeletedFalse(userRoleModel.getRoleId()).ifPresent(new Consumer<Role>() {
										@Override
										public void accept(Role role) {
											userRepository.findOneByIdAndDeletedFalse(userRoleModel.getUserId())
													.ifPresent(new Consumer<User>() {
														@Override
														public void accept(User user1) {
															var baseNotificationMessage = new BaseNotificationMessage(
																	ZonedDateTime.now(), APP,
																	new From(user.getId(), user.getUserName()),
																	new Target(ROLE, userRoleModel.getRoleId().toString(), UPDATE));
															amqNotifier
																	.send(amqNotifier.saveMessage(user1, baseNotificationMessage));
														}
													});
										}
									});
								}
							});
				}
			} catch (IOException e1) {
				logger.error(e.getMessage());
			}
		}
	}

	@Override
	protected void onUpdate(BaseMessage notificationMessage) {
		var roleId = Long.valueOf((Integer) notificationMessage.getEntityId());
		userRepository.findOneByUserNameAndDeletedFalse(notificationMessage.getUserName()).ifPresent(new Consumer<User>() {
			@Override
			public void accept(User user) {
				roleRepository.findOneByIdAndDeletedFalse(roleId).ifPresent(new Consumer<Role>() {
					@Override
					public void accept(Role role) {
						var baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(), APP,
								new From(user.getId(), user.getUserName()), new Target(ROLE, roleId.toString(), UPDATE));
						userRepository.findByRolesIdInAndDeletedFalse(role.getId()).forEach(new Consumer<User>() {
							@Override
							public void accept(User user1) {
								amqNotifier.send(amqNotifier.saveMessage(user1, baseNotificationMessage));
							}
						});
					}
				});
			}
		});
	}
}
