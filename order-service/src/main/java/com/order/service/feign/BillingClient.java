package com.order.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.order.service.request.InvoiceRequest;

@FeignClient(name = "billing-service", url = "http://localhost:8083")
public interface BillingClient {
    @PostMapping("/api/billing/create/invoice")
    void createInvoice(@RequestBody InvoiceRequest request);
}
