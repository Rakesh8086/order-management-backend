package com.order.service.response;

import java.time.LocalDate;
import java.util.List;

import com.order.service.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
	private Long id;
	private Long userId;
	private LocalDate orderDate;
	private LocalDate deliveryDate;
	private Double totalAmount;
	private OrderStatus status;
	private String address;
	private List<OrderItemResponse> items;
}
