package com.notification.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notification.service.request.NotificationRequest;
import com.notification.service.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/send/email")
    public ResponseEntity<String> sendOrderNotification(@RequestBody NotificationRequest request) {
        notificationService.sendOrderConfirmation(request);
        return ResponseEntity.ok("Notification processed and saved.");
    }
}
