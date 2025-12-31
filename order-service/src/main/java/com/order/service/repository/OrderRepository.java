package com.order.service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.order.service.entity.Order;
import com.order.service.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUserId(Long id);
	@Query("SELECT o FROM Order o WHERE " +
		       "(:status IS NULL OR o.status = :status) AND " +
		       "(:userId IS NULL OR o.userId = :userId) AND " +
		       "(:startDate IS NULL OR o.orderDate >= :startDate) " +
		       "ORDER BY o.orderDate DESC")
		List<Order> findOrdersByAdminFilters(
		    @Param("status") OrderStatus status, 
		    @Param("userId") Long userId,
		    @Param("startDate") LocalDateTime startDate
		);
}	
