package com.order.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.order.service.request.NotificationRequest;

@FeignClient(name = "notification-service", url = "http://notification-service:8084")
public interface NotificationClient {

    @PostMapping("/api/notifications/send/email")
    void sendOrderNotification(@RequestBody NotificationRequest request);
}
