package com.order.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.order.service.request.OrderItemRequest;
import com.order.service.request.OrderRequest;
import com.order.service.response.OrderResponse;
import com.order.service.response.ProductResponseAdmin;
import com.order.service.service.impl.OrderServiceImpl;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private BillingClient billingClient;
    @Mock
    private NotificationClient notificationClient;
    @InjectMocks
    private OrderServiceImpl orderService;
    private OrderRequest orderRequest;
    private OrderItemRequest itemRequest;
    private ProductResponseAdmin product;
    
    @BeforeEach
    void setup() {
        itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        orderRequest = new OrderRequest();
        orderRequest.setAddress("Bangalore");
        orderRequest.setDeliveryWithinDays(2);
        orderRequest.setItems(List.of(itemRequest));
        product = new ProductResponseAdmin();
        product.setId(1L);
        product.setName("Phone");
        product.setBrand("Samsung");
        product.setFinalPrice(10000.0);
        product.setCurrentStock(10);
    }
    
    @Test
    void placeOrder_success() {
        when(productClient.getById(1L))
                .thenReturn(product);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> { Order o = inv.getArgument(0);
                    o.setId(1L);
                    return o;
                });
        Long orderId = orderService.placeOrder(orderRequest, 99L);
        assertEquals(1L, orderId);
        verify(productClient).updateStock(1L, -2);
        verify(billingClient).createInvoice(any());
        verify(notificationClient).sendOrderNotification(any());
    }
    
    @Test
    void placeOrder_productNotFound() {
        when(productClient.getById(1L))
                .thenThrow(FeignException.NotFound.class);
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.placeOrder(orderRequest, 99L));
    }
    
    @Test
    void placeOrder_insufficientStock() {
        product.setCurrentStock(1);
        when(productClient.getById(1L))
                .thenReturn(product);
        assertThrows(InsufficientStockException.class,
                () -> orderService.placeOrder(orderRequest, 99L));
    }
    
    @Test
    void placeOrder_billingFailure() {
        when(productClient.getById(1L))
                .thenReturn(product);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(inv -> {Order o = inv.getArgument(0);
                    o.setId(1L);
                    return o;
                });
        doThrow(new RuntimeException("billing down"))
                .when(billingClient)
                .createInvoice(any());
        assertThrows(ServiceDownException.class,
                () -> orderService.placeOrder(orderRequest, 99L));
        verify(productClient).updateStock(1L, -2);
        verify(productClient).updateStock(1L, 2); 
    }
    
    @Test
    void getById_success() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryWithinDays(2);
        order.setStatus(OrderStatus.ORDERED);
        order.setItems(new ArrayList<>());
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        OrderResponse response = orderService.getById(1L);
        assertEquals(1L, response.getId());
    }
    
    @Test
    void getById_notFound() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.getById(1L));
    }
    
    @Test
    void getOrderHistory_success() {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryWithinDays(1);
        order.setItems(new ArrayList<>());
        when(orderRepository.findByUserId(10L))
                .thenReturn(List.of(order));
        List<OrderResponse> result = orderService.getOrderHistory(10L);
        assertEquals(1, result.size());
    }
    
    @Test
    void cancelOrder_success() {
        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);
        Order order = new Order();
        order.setStatus(OrderStatus.ORDERED);
        order.setOrderDate(LocalDateTime.now().minusHours(1));
        order.setDeliveryWithinDays(2);
        order.setItems(List.of(item));
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        orderService.cancelOrder(1L);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(productClient).updateStock(1L, 2);
    }
    
    @Test
    void cancelOrder_failure() {
        Order order = new Order();
        order.setStatus(OrderStatus.ORDERED);
        order.setOrderDate(LocalDateTime.now().minusDays(5));
        order.setDeliveryWithinDays(1);
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        assertThrows(CancellationNotPossibleException.class,
                () -> orderService.cancelOrder(1L));
    }
    
    @Test
    void getAllOrdersByFilters_success() {
        AdminSearchFilter filter = new AdminSearchFilter();
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryWithinDays(1);
        order.setItems(new ArrayList<>());
        when(orderRepository.findOrdersByAdminFilters(
                any(), any(), any()))
                .thenReturn(List.of(order));
        List<OrderResponse> result =
                orderService.getAllOrdersByFilters(filter);
        assertEquals(1, result.size());
    }
    
    @Test
    void updateOrderStatus_marksDelivered() {
        Order order = new Order();
        order.setStatus(OrderStatus.ORDERED);
        order.setOrderDate(LocalDateTime.now().minusDays(5));
        order.setDeliveryWithinDays(2);
        when(orderRepository.findOrdersByAdminFilters(
                eq(OrderStatus.ORDERED), any(), any()))
                .thenReturn(List.of(order));
        orderService.updateOrderStatus(new AdminSearchFilter());
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(orderRepository).saveAll(any());
    }
}
