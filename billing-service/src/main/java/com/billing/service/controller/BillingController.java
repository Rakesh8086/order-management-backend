package com.billing.service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billing.service.request.InvoiceRequest;
import com.billing.service.response.FinanceReportResponse;
import com.billing.service.service.BillingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {
	private final BillingService billingService;
	
	@PostMapping("/create/invoice")
    public ResponseEntity<Long> createInvoice(
    		@Valid @RequestBody InvoiceRequest request) {
        return new ResponseEntity<>(
        		billingService.createInvoice(request), HttpStatus.CREATED);
    }
	
	@GetMapping("/report")
    public ResponseEntity<FinanceReportResponse> getFinanceReport() {
        return ResponseEntity.ok(billingService.getFinanceReport());
    }
}
