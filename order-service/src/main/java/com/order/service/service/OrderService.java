package com.order.service.service;

import java.util.List;

import com.order.service.request.AdminSearchFilter;
import com.order.service.request.OrderRequest;
import com.order.service.response.OrderResponse;

public interface OrderService {
	Long placeOrder(OrderRequest request, Long userId);
	OrderResponse getById(Long id);
	List<OrderResponse> getOrderHistory(Long id);
	void cancelOrder(Long orderId);
	List<OrderResponse> getAllOrdersByFilters(AdminSearchFilter filter);
}
