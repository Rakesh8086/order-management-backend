package com.notification.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notification.service.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
	
}
