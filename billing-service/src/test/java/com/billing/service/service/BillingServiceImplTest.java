package com.billing.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.billing.service.entity.Invoice;
import com.billing.service.repository.InvoiceRepository;
import com.billing.service.request.InvoiceRequest;
import com.billing.service.response.FinanceReportResponse;
import com.billing.service.response.InvoiceResponse;
import com.billing.service.service.impl.BillingServiceImpl;

@ExtendWith(MockitoExtension.class)
class BillingServiceImplTest {
    @Mock
    private InvoiceRepository invoiceRepository;
    @InjectMocks
    private BillingServiceImpl billingService;
    private InvoiceRequest request;
    private Invoice invoice;
    
    @BeforeEach
    void setup() {
        request = new InvoiceRequest();
        request.setOrderId(1L);
        request.setUserId(10L);
        request.setTotalAmount(2500.0);

        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setOrderId(1L);
        invoice.setUserId(10L);
        invoice.setTotalAmount(2500.0);
        invoice.setInvoiceDate(LocalDateTime.now());
    }
    
    @Test
    void createInvoice_success() {
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation -> {
                    Invoice saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });
        Long invoiceId = billingService.createInvoice(request);
        assertEquals(1L, invoiceId);
        verify(invoiceRepository).save(any(Invoice.class));
    }
    
    @Test
    void getFinanceReport_success() {
        when(invoiceRepository.getTotalRevenue())
                .thenReturn(10000.0);
        when(invoiceRepository.count())
                .thenReturn(4L);
        when(invoiceRepository.getAverageInvoiceValue())
                .thenReturn(2500.0);
        FinanceReportResponse response =
                billingService.getFinanceReport();
        assertEquals(10000.0, response.getTotalRevenue());
        assertEquals(4L, response.getTotalOrders());
        assertEquals(2500.0, response.getAverageOrderValue());
        assertNotNull(response.getReportGeneratedDate());
        assertNotNull(response.getReportGeneratedTime());
    }
    
    @Test
    void getByOrderId_success() {
        when(invoiceRepository.findByOrderId(1L))
                .thenReturn(Optional.of(invoice));
        InvoiceResponse response =
                billingService.getByOrderId(1L);
        assertEquals(1L, response.getOrderId());
        assertEquals(2500.0, response.getTotalAmount());
    }
    
    @Test
    void getAllInvoicesByUserId_success() {
        when(invoiceRepository.findByUserId(anyLong()))
                .thenReturn(List.of(invoice));
        List<InvoiceResponse> responses =
                billingService.getAllInvoicesByUserId(10L);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getOrderId());
    }
}
