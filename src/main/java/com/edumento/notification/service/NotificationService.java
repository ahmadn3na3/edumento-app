package com.edumento.notification.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.core.constants.notification.MessageCategory;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.notification.domian.Notification;
import com.edumento.notification.models.NotificationMessage;
import com.edumento.notification.repo.NotificationRepository;
import com.edumento.user.domain.User;
import com.edumento.user.repo.UserRepository;

/** Created by ayman on 15/08/17. */
@Service
public class NotificationService {

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	UserRepository userRepository;

	@Transactional
	public ResponseModel acknowledge(String[] id) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				notificationRepository.findByIdInAndUserIdAndReceivedFalseAndDeletedFalseOrderByCreationDateDesc(
						Arrays.asList(id), user.getId()).forEach(new Consumer<Notification>() {
							@Override
							public void accept(Notification notification) {
								notification.setReceived(true);
								notificationRepository.save(notification);
							}
						});
				return ResponseModel.done();
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	public ResponseModel getMyNotifications(boolean seen) {
		List<Notification> notificationList = new ArrayList<>();
		List<NotificationMessage> notificationMessages = notificationRepository
				.findByUserIdAndReceivedFalseAndDeletedFalseOrderByCreationDateDesc(
						SecurityUtils.getCurrentUser().getId())
				.map(new Function<Notification, NotificationMessage>() {
					@Override
					public NotificationMessage apply(Notification notification) {
						notification.setReceived(seen);
						notificationList.add(notification);
						return new NotificationMessage(notification);
					}
				}).collect(Collectors.toList());
		if (!notificationList.isEmpty()) {
			notificationRepository.saveAll(notificationList);
		}
		return ResponseModel.done(notificationMessages);
	}

	@Transactional
	public ResponseModel getMyNotificationsCount() {
		return ResponseModel
				.done(notificationRepository.countByUserIdAndReceivedFalseAndDeletedFalseAndNotificationCategoryEquals(
						SecurityUtils.getCurrentUser().getId(), MessageCategory.USER).get());
	}

	@Transactional
	public PageResponseModel viewAll(PageRequest pageRequestModel) {

		var notificationPage = notificationRepository
				.findByUserIdAndDeletedFalseAndNotificationCategoryEqualsOrderByCreationDateDesc(
						SecurityUtils.getCurrentUser().getId(), pageRequestModel, MessageCategory.USER);

		return PageResponseModel.done(
				notificationPage.getContent().stream().map(NotificationMessage::new).collect(Collectors.toList()),
				notificationPage.getTotalPages(), pageRequestModel.getPageNumber(),
				notificationPage.getTotalElements());
	}

	@Transactional
	public ResponseModel seen(String... id) {
		return ResponseModel.done(
				notificationRepository.findByIdInAndUserIdAndDeletedFalseOrderByCreationDateDesc(Arrays.asList(id),
						SecurityUtils.getCurrentUser().getId()).map(new Function<Notification, String>() {
							@Override
							public String apply(Notification notification) {
								notification.setSeen(true);
								notificationRepository.save(notification);
								return notification.getId();
							}
						}).collect(Collectors.toList()));
	}
}
