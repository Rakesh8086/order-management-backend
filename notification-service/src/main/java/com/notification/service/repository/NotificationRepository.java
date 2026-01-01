package com.notification.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notification.service.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
	Optional<Notification> findByOrderId(Long orderId);
}
