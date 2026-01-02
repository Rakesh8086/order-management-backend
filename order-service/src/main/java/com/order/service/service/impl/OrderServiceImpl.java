package com.order.service.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.order.service.entity.Order;
import com.order.service.entity.OrderItem;
import com.order.service.entity.OrderStatus;
import com.order.service.exception.CancellationNotPossibleException;
import com.order.service.exception.InsufficientStockException;
import com.order.service.exception.ResourceNotFoundException;
import com.order.service.exception.ServiceDownException;
import com.order.service.feign.BillingClient;
import com.order.service.feign.NotificationClient;
import com.order.service.feign.ProductClient;
import com.order.service.repository.OrderRepository;
import com.order.service.request.AdminSearchFilter;
import com.order.service.request.InvoiceRequest;
import com.order.service.request.NotificationRequest;
import com.order.service.request.OrderItemRequest;
import com.order.service.request.OrderRequest;
import com.order.service.response.OrderItemResponse;
import com.order.service.response.OrderResponse;
import com.order.service.response.ProductResponseAdmin;
import com.order.service.service.OrderService;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final BillingClient billingClient;
    private final NotificationClient notificationClient;

    @Override
    // @Transactional 
    public Long placeOrder(OrderRequest request, Long userId) {
        Order order = new Order();
        order.setUserId(9876L);
        order.setAddress(request.getAddress());
        order.setDeliveryWithinDays(request.getDeliveryWithinDays());
        order.setStatus(OrderStatus.ORDERED);
        order.setOrderDate(LocalDateTime.now());

        Double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for(OrderItemRequest item : request.getItems()) {
        	ProductResponseAdmin product;
        	try {
        		product = productClient.getById(
        						item.getProductId());
        	} 
        	catch(FeignException.NotFound e) {
        	    throw new ResourceNotFoundException(
        	    		"Product not found with Id: " + 
        	    				item.getProductId());
        	} 
            if(item.getQuantity() > product.getCurrentStock()) {
                throw new InsufficientStockException(
                		"only " + product.getCurrentStock() +
                		" available for " + product.getName() + 
                		" for the brand " + product.getBrand());
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(product.getName());
            orderItem.setBrand(product.getBrand());
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
        
        try {
        	billingClient.createInvoice(new InvoiceRequest(
                    order.getId(), order.getUserId(), order.getTotalAmount()
            ));
        	try {
            	NotificationRequest notification = new NotificationRequest();
                notification.setOrderId(savedOrder.getId());
                notification.setRecipientEmail("metal01spike@gmail.com");      
                notificationClient.sendOrderNotification(notification);
            }
        	catch (Exception e) {
        		System.err.println("Notification service down*******");
        		// throw new ServiceDownException("Notification service down, "
                // 		+ "but order confirmed"); 
        	}
        } 
        catch (Exception e) {
        	order.setStatus(OrderStatus.FAILED);
        	for(OrderItem item : order.getItems()) {
                productClient.updateStock(item.getProductId(), item.getQuantity());
            }
        	orderRepository.save(order);
            System.err.println("Billing service down*********");
            throw new ServiceDownException("Billing service down, "
            		+ "order failed"); 
        }
        
		return savedOrder.getId();
    }
    
    @Override
    public OrderResponse getById(Long id) {
    	Optional<Order> orderOptional = orderRepository.findById(id);
    	if(!orderOptional.isPresent()) {
    		throw new ResourceNotFoundException(
    				"Order not found with id: "+ id);
    	}
    	Order order = orderOptional.get();
    	OrderResponse response = mapEntityToResponse(order);
    	
    	return response;
    }
    
    @Override
    public List<OrderResponse> getOrderHistory(Long id){
    	List<Order> allOrders = orderRepository.findByUserId(id);
    	List<OrderResponse> allResponse = new ArrayList<>();
    	for(Order order: allOrders) {
    		OrderResponse response = mapEntityToResponse(order);
    		allResponse.add(response);
    	}
    	
    	return allResponse;
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if(!orderOptional.isPresent()) {
        	throw new ResourceNotFoundException(
        			"Order not found with id " + orderId);
        }
        Order order = orderOptional.get();
        if(!order.getStatus().equals(OrderStatus.ORDERED)) {
            throw new IllegalStateException(
            		"Order cannot be canceled once it is " +
            				order.getStatus());
        }
        LocalDateTime deliveryDate = order.getOrderDate().plusDays(order.getDeliveryWithinDays());
        if(ChronoUnit.DAYS.between(LocalDateTime.now(), deliveryDate) <= 0) {
        	throw new CancellationNotPossibleException(
        			"Cancellation failed. Order must be cancelled at least "
							+ "24 hours prior to delivery date");
        }
        // update stock
        List<OrderItem> orderedItems = order.getItems();
        for(OrderItem item : orderedItems) {
            productClient.updateStock(item.getProductId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
    
    @Override
    public List<OrderResponse> getAllOrdersByFilters(
    		AdminSearchFilter filter){
    	List<Order> allOrders = orderRepository.findOrdersByAdminFilters(
    			filter.getStatus(), filter.getUserId(), 
    			filter.getStartDate());
    	List<OrderResponse> allResponse = new ArrayList<>();
    	for(Order order: allOrders) {
    		OrderResponse response = mapEntityToResponse(order);
    		allResponse.add(response);
    	}
    	
    	return allResponse;
    }
    
    @Override
    public void updateOrderStatus(AdminSearchFilter filter) {
    	List<Order> pendingOrders = orderRepository.findOrdersByAdminFilters(
    			OrderStatus.ORDERED, filter.getUserId(), 
    			filter.getStartDate());
    	LocalDateTime currDateTime = LocalDateTime.now();
    	// System.out.println("((((((((((" + pendingOrders.size());
    	for(Order order: pendingOrders) {
    		LocalDateTime deliveryDate = order.getOrderDate()
                    .plusDays(order.getDeliveryWithinDays());
    		// System.out.println("deliveryDate &&&&&&& " + deliveryDate);
    		// System.out.println("Now ******* " + currDateTime);
			if(currDateTime.isAfter(deliveryDate)) {	
				// System.out.println("here " + order.getId());
				order.setStatus(OrderStatus.DELIVERED);
			}
    	}
    	orderRepository.saveAll(pendingOrders);
    }
    
    private OrderResponse mapEntityToResponse(Order order) {
    	OrderResponse response = new OrderResponse();
    	response.setId(order.getId());
    	response.setOrderDate(order.getOrderDate().toLocalDate());
    	response.setDeliveryDate(order.getOrderDate().plusDays(
    					order.getDeliveryWithinDays()).toLocalDate());
    	response.setTotalAmount(order.getTotalAmount());
    	response.setStatus(order.getStatus());
    	response.setAddress(order.getAddress());
    	List<OrderItemResponse> itemsResponse = new ArrayList<>();
    	List<OrderItem> items = order.getItems();
    	// during place order, we send feign call to fetch unit price 
    	// using Id and store it in OrderItem entity
    	// during get order, we retrieve unit price of each product 
    	// from OrderItem entity 
    	for(OrderItem item: items) {
    		OrderItemResponse detail = new OrderItemResponse();
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setProductName(item.getProductName());
            detail.setBrand(item.getBrand());
            itemsResponse.add(detail);
    	}
    	response.setItems(itemsResponse);
    	
    	return response;
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