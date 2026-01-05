package com.order.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.service.request.AdminSearchFilter;
import com.order.service.request.OrderRequest;
import com.order.service.response.OrderResponse;
import com.order.service.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	
	@PostMapping("/order")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
	public ResponseEntity<Long> placeOrder(
	        @Valid @RequestBody OrderRequest request, 
	        @RequestHeader("X-Authenticated-UserId") String userId) {

	    Long orderId = orderService.placeOrder(request, Long.valueOf(userId));
	    
	    return new ResponseEntity<>(orderId, HttpStatus.CREATED);
	}
	
	@GetMapping("/id/{id}")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
	public ResponseEntity<OrderResponse> getById(@PathVariable Long id){
		return new ResponseEntity<>(orderService.getById(id), 
				HttpStatus.OK);
	}
	
	@GetMapping("/history/{id}")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
	public ResponseEntity<List<OrderResponse>> 
	getOrderHistory(@PathVariable Long id){
		return new ResponseEntity<>(orderService.getOrderHistory(id), 
				HttpStatus.OK);
	}
	
	@PutMapping("/cancel/{id}")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
	public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
	    orderService.cancelOrder(id);
	    return ResponseEntity.ok("Order cancelled.");
	}
	
	@GetMapping("/admin/filter")
	@PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
	public ResponseEntity<List<OrderResponse>> 
	getAllOrdersByFilters(@RequestBody AdminSearchFilter filter){
		return new ResponseEntity<>(
				orderService.getAllOrdersByFilters(filter), 
				HttpStatus.OK);
	}
	
	@PostMapping("/update/order/status")
	@PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
	public ResponseEntity<String> updateOrderStatus(
			@RequestBody AdminSearchFilter filter) {
	    orderService.updateOrderStatus(filter);
	    return ResponseEntity.ok("Delivery status updated");
	}
}
