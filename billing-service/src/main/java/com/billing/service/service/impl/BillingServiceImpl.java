package com.billing.service.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.billing.service.entity.Invoice;
import com.billing.service.repository.InvoiceRepository;
import com.billing.service.request.InvoiceRequest;
import com.billing.service.response.FinanceReportResponse;
import com.billing.service.service.BillingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public Long createInvoice(InvoiceRequest request) {
        Invoice invoice = new Invoice();
        invoice.setOrderId(request.getOrderId());
        invoice.setUserId(request.getUserId());
        invoice.setTotalAmount(request.getTotalAmount());
        invoice.setInvoiceDate(LocalDateTime.now()); 
        invoiceRepository.save(invoice);
        
        return invoice.getId();
    }
    
    @Override
    public FinanceReportResponse getFinanceReport() {
        Double totalRevenue = invoiceRepository.getTotalRevenue();
        Long totalOrders = invoiceRepository.count();
        Double avgValue = invoiceRepository.getAverageInvoiceValue();

        FinanceReportResponse report = new FinanceReportResponse();
        report.setTotalRevenue(totalRevenue);
        report.setTotalOrders(totalOrders);
        report.setAverageOrderValue(avgValue);
        report.setReportGeneratedDate(LocalDate.now());
        report.setReportGeneratedTime(LocalTime.now());
        
        return report;
    }
}
