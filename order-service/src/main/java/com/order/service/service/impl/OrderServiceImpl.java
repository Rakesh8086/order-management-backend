package com.order.service.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.order.service.entity.Order;
import com.order.service.entity.OrderItem;
import com.order.service.entity.OrderStatus;
import com.order.service.exception.InsufficientStockException;
import com.order.service.feign.ProductClient;
import com.order.service.repository.OrderRepository;
import com.order.service.request.OrderItemRequest;
import com.order.service.request.OrderRequest;
import com.order.service.response.ProductResponseAdmin;
import com.order.service.service.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient; 

    @Override
    @Transactional
    public Long placeOrder(OrderRequest request, Long userId) {
        Order order = new Order();
        order.setShippingAddress(request.getShippingAddress());
        order.setDeliveryWithinDays(request.getDeliveryWithinDays());
        order.setStatus(OrderStatus.ORDERED);
        order.setOrderDate(LocalDateTime.now().plusMinutes(1));

        Double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest item : request.getItems()) {
        	ProductResponseAdmin product = productClient.getById(item.getProductId());
            if(item.getQuantity() > product.getCurrentStock()) {
                throw new InsufficientStockException(
                		"only " + product.getCurrentStock() +
                		"available for " + product.getName());
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(product.getFinalPrice()); 
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            totalAmount += product.getFinalPrice() * item.getQuantity();
            productClient.updateStock(
            		item.getProductId(), -item.getQuantity());
        }
        totalAmount += DeliveryFee(request.getDeliveryWithinDays());
        
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        // order items saved automatically because of cascade
        Order savedOrder = orderRepository.save(order);
        
        return savedOrder.getId();
    }
    
    private Double DeliveryFee(Integer deliveryWithinDays) {
    	if(deliveryWithinDays == 1) {
    		return 100.0;
    	}
    	else if(deliveryWithinDays == 2) {
    		return 80.0;
    	}
    	else if(deliveryWithinDays == 3) {
    		return 60.0;
    	}
    	else if(deliveryWithinDays == 4) {
    		return 50.0;
    	}
    	
    	return 40.0;
    }
}