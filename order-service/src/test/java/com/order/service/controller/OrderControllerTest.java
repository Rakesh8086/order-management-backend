package com.order.service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.request.AdminSearchFilter;
import com.order.service.request.OrderItemRequest;
import com.order.service.request.OrderRequest;
import com.order.service.response.OrderResponse;
import com.order.service.service.OrderService;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;
    private OrderRequest orderRequest;
    
    @BeforeEach
    void setup() {
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        orderRequest = new OrderRequest();
        orderRequest.setAddress("Bangalore");
        orderRequest.setDeliveryWithinDays(2);
        orderRequest.setItems(List.of(item));
    }
    
    @Test
    void placeOrder_success() throws Exception {
        when(orderService.placeOrder(any(), any()))
                .thenReturn(10L);
        mockMvc.perform(post("/api/orders/order")
        		.header("X-Authenticated-UserId", "5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("10"));
    }
    
    @Test
    void placeOrder_validationError() throws Exception {
        OrderRequest invalid = new OrderRequest();
        mockMvc.perform(post("/api/orders/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getById_success() throws Exception {
        OrderResponse response = new OrderResponse();
        response.setId(1L);
        when(orderService.getById(1L))
                .thenReturn(response);
        mockMvc.perform(get("/api/orders/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
    
    @Test
    void cancelOrder_success() throws Exception {
        doNothing().when(orderService).cancelOrder(1L);
        mockMvc.perform(put("/api/orders/cancel/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order cancelled."));
    }
    
    @Test
    void getAllOrdersByFilters_success() throws Exception {
        AdminSearchFilter filter = new AdminSearchFilter();
        when(orderService.getAllOrdersByFilters(any()))
                .thenReturn(List.of(new OrderResponse()));
        mockMvc.perform(post("/api/orders/admin/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    void updateOrderStatus_success() throws Exception {
        AdminSearchFilter filter = new AdminSearchFilter();
        doNothing().when(orderService)
                .updateOrderStatus(any());
        mockMvc.perform(post("/api/orders/update/order/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Delivery status updated"));
    }
}


