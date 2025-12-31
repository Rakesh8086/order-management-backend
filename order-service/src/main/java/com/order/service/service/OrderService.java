package com.order.service.service;

import com.order.service.request.OrderRequest;
import com.order.service.response.OrderResponse;

public interface OrderService {
	Long placeOrder(OrderRequest request, Long userId);
	OrderResponse getById(Long id);
}
