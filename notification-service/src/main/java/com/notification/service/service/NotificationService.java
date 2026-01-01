package com.notification.service.service;

import com.notification.service.request.NotificationRequest;

public interface NotificationService {
	void sendOrderConfirmation(NotificationRequest request);
}
