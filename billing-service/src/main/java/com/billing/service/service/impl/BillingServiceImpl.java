package com.billing.service.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.billing.service.entity.Invoice;
import com.billing.service.exception.ResourceNotFoundException;
import com.billing.service.repository.InvoiceRepository;
import com.billing.service.request.InvoiceRequest;
import com.billing.service.response.FinanceReportResponse;
import com.billing.service.response.InvoiceResponse;
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
    
    @Override
    public InvoiceResponse getByOrderId(Long orderId) {
    	Optional<Invoice> invoiceOptional = invoiceRepository.
    			findByOrderId(orderId);
    	if(!invoiceOptional.isPresent()) {
    		throw new ResourceNotFoundException("Order not found with "
    				+ "id " + orderId);
    	}
    	Invoice invoice = invoiceOptional.get();
    	
    	return mapEntityToResponse(invoice);
    }
    
    @Override
    public List<InvoiceResponse> getAllInvoicesByUserId(Long userId){
    	List<Invoice> allInvoice = invoiceRepository.
    			findByUserId(userId);
    	List<InvoiceResponse> allResponse = new ArrayList<>();
    	for(Invoice invoice: allInvoice) {
    		InvoiceResponse response = mapEntityToResponse(invoice);
    		allResponse.add(response);
    	}
    	
    	return allResponse;
    }
    
    private InvoiceResponse mapEntityToResponse(Invoice invoice) {
    	InvoiceResponse response = new InvoiceResponse();
    	response.setId(invoice.getId());
    	response.setOrderId(invoice.getOrderId());
    	response.setTotalAmount(invoice.getTotalAmount());
    	response.setInvoiceDate(invoice.getInvoiceDate().toLocalDate());
    	response.setInvoiceTime(invoice.getInvoiceDate().toLocalTime());
    	
    	return response;
    }
}
