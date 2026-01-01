package com.notification.service.service;

import com.notification.service.request.NotificationRequest;
import com.notification.service.response.NotificationResponse;

public interface NotificationService {
	void sendOrderConfirmation(NotificationRequest request);
	NotificationResponse getNotificationByOrderId(Long id);
}
