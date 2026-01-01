package com.billing.service.service;

import com.billing.service.request.InvoiceRequest;
import com.billing.service.response.FinanceReportResponse;

public interface BillingService {
	Long createInvoice(InvoiceRequest request);
	FinanceReportResponse getFinanceReport();
}
