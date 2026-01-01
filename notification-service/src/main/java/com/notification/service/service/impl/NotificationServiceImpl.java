package com.notification.service.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.notification.service.entity.Notification;
import com.notification.service.entity.Status;
import com.notification.service.exception.ResourceNotFoundException;
import com.notification.service.repository.NotificationRepository;
import com.notification.service.request.NotificationRequest;
import com.notification.service.response.NotificationResponse;
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
    
    @Override
    public NotificationResponse getNotificationByOrderId(Long orderId) {
    	Optional<Notification> notificationOptional =
    			notificationRepository.findByOrderId(orderId);
    	if(!notificationOptional.isPresent()) {
    		throw new ResourceNotFoundException(
    				"Notification not found with order id " + orderId);
    	}
    	Notification notification = notificationOptional.get();
    	NotificationResponse response = new NotificationResponse();
    	response.setId(notification.getId());
    	response.setOrderId(notification.getOrderId());
    	response.setRecipientEmail(notification.getRecipientEmail());
    	response.setTimestamp(notification.getTimestamp());
    	response.setErrorMessage(notification.getErrorMessage());
    	response.setStatus(notification.getStatus());
    	
    	return response;
    }
}
