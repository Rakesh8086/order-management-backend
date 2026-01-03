package com.billing.service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.billing.service.exception.ResourceNotFoundException;
import com.billing.service.request.InvoiceRequest;
import com.billing.service.response.FinanceReportResponse;
import com.billing.service.response.InvoiceResponse;
import com.billing.service.service.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BillingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BillingService billingService;
    @Autowired
    private ObjectMapper objectMapper;
    private InvoiceRequest invoiceRequest;
    
    @BeforeEach
    void setup() {
        invoiceRequest = new InvoiceRequest();
        invoiceRequest.setOrderId(1L);
        invoiceRequest.setUserId(10L);
        invoiceRequest.setTotalAmount(2500.0);
    }
    
    @Test
    void createInvoice_success() throws Exception {
        when(billingService.createInvoice(any()))
                .thenReturn(5L);
        mockMvc.perform(post("/api/billing/create/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invoiceRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("5"));
    }
    
    @Test
    void getFinanceReport_success() throws Exception {
        FinanceReportResponse response = new FinanceReportResponse();
        response.setTotalRevenue(10000.0);
        response.setTotalOrders(4L);
        response.setAverageOrderValue(2500.0);
        when(billingService.getFinanceReport())
                .thenReturn(response);
        mockMvc.perform(get("/api/billing/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRevenue").value(10000.0))
                .andExpect(jsonPath("$.totalOrders").value(4));
    }
    
    @Test
    void getByOrderId_success() throws Exception {
        InvoiceResponse response = new InvoiceResponse();
        response.setOrderId(1L);
        response.setTotalAmount(2500.0);
        when(billingService.getByOrderId(1L))
                .thenReturn(response);
        mockMvc.perform(get("/api/billing/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(2500.0));
    }
    
    @Test
    void getAllInvoicesByUserId_success() throws Exception {
        when(billingService.getAllInvoicesByUserId(10L))
                .thenReturn(List.of(new InvoiceResponse()));
        mockMvc.perform(get("/api/billing/history/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    void getByOrderId_notFound() throws Exception {
        when(billingService.getByOrderId(1L))
                .thenThrow(new ResourceNotFoundException(
                        "Order not found with id 1"));
        mockMvc.perform(get("/api/billing/order/1"))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string("Order not found with id 1"));
    }
    
    @Test
    void createInvoice_validationError() throws Exception {
        InvoiceRequest invalid = new InvoiceRequest();
        mockMvc.perform(post("/api/billing/create/invoice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
