package com.order.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<Long> placeOrder(
	        @Valid @RequestBody OrderRequest request, 
	        Long userId) {
	    Long orderId = orderService.placeOrder(request, userId);
	    
	    return new ResponseEntity<>(orderId, HttpStatus.CREATED);
	}
	
	@GetMapping("/id/{id}")
	public ResponseEntity<OrderResponse> getById(@PathVariable Long id){
		return new ResponseEntity<>(orderService.getById(id), 
				HttpStatus.OK);
	}
}
