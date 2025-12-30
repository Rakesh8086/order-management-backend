package com.order.service.service;

import com.order.service.request.OrderRequest;

public interface OrderService {
	Long placeOrder(OrderRequest request, Long userId);
}
