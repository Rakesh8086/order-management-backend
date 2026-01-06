package com.billing.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billing.service.request.InvoiceRequest;
import com.billing.service.response.FinanceReportResponse;
import com.billing.service.response.InvoiceResponse;
import com.billing.service.service.BillingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {
	private final BillingService billingService;
	
	@PostMapping("/create/invoice")
	// @PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
    public ResponseEntity<Long> createInvoice(
    		@Valid @RequestBody InvoiceRequest request) {
        return new ResponseEntity<>(
        		billingService.createInvoice(request), HttpStatus.CREATED);
    }
	
	@GetMapping("/report")
	@PreAuthorize("hasRole('FINANCE_OFFICER')")
    public ResponseEntity<FinanceReportResponse> getFinanceReport() {
        return ResponseEntity.ok(billingService.getFinanceReport());
    }
	
	@GetMapping("/order/{orderId}")
	@PreAuthorize("hasRole('FINANCE_OFFICER')")
    public ResponseEntity<InvoiceResponse> getByOrderId(
    		@PathVariable Long orderId) {  
    		return new ResponseEntity<>(
    					billingService.getByOrderId(orderId), 
    			HttpStatus.OK);
    }
	
	@GetMapping("/history")
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoicesByUserId(
    		@RequestHeader("X-Authenticated-UserId") String userId) {  
    		return new ResponseEntity<>(
    					billingService.getAllInvoicesByUserId(Long.valueOf(userId)), 
    			HttpStatus.OK);
    }
}
