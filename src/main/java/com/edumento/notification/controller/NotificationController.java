package com.edumento.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.notification.service.NotificationService;

/** Created by ayman on 15/08/17. */
@RestController
@RequestMapping("/api/notification")
public class NotificationController extends AbstractController {

	@Autowired
	NotificationService notificationService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseModel acknowledge(@RequestParam String[] id) {
		return notificationService.acknowledge(id);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseModel getMyNotifications(@RequestHeader(name = "seen", required = false) boolean seen) {
		return notificationService.getMyNotifications(seen);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/count")
	public ResponseModel getMyNotifications() {
		return notificationService.getMyNotificationsCount();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/view")
	public ResponseModel viewNotifications(@RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size) {
		return notificationService.viewAll(PageRequestModel.getPageRequestModel(page, size));
	}

	@RequestMapping(method = RequestMethod.POST, path = "/seen")
	public ResponseModel seen(@RequestParam String[] id) {
		return notificationService.seen(id);
	}
}
