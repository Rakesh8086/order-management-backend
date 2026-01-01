package com.billing.service.service;

import com.billing.service.request.InvoiceRequest;

public interface BillingService {
	Long createInvoice(InvoiceRequest request);
}
