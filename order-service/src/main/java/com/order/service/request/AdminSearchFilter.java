package com.order.service.request;

import java.time.LocalDateTime;

import com.order.service.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminSearchFilter {
	private OrderStatus status;
	private Long userId;
	private LocalDateTime startDate;
}
