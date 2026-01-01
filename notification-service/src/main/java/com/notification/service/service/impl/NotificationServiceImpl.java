package com.notification.service.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.notification.service.entity.Notification;
import com.notification.service.entity.Status;
import com.notification.service.repository.NotificationRepository;
import com.notification.service.request.NotificationRequest;
import com.notification.service.service.EmailService;
import com.notification.service.service.NotificationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
	private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void sendOrderConfirmation(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setOrderId(request.getOrderId());
        notification.setTimestamp(LocalDateTime.now());
        try {
            String subject = "Order Confirmation - #" + request.getOrderId();
            String body = "Your order #" + request.getOrderId() + " has been placed successfully";
            emailService.sendEmail(request.getRecipientEmail(), subject, body);
            notification.setStatus(Status.SENT);
        }
        catch (Exception e) {
        	notification.setStatus(Status.FAILED);
        	notification.setErrorMessage(e.getMessage());
        }

        notificationRepository.save(notification);
    }
}
