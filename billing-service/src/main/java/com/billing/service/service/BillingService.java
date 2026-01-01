package com.billing.service.service;

import java.util.List;

import com.billing.service.request.InvoiceRequest;
import com.billing.service.response.FinanceReportResponse;
import com.billing.service.response.InvoiceResponse;

public interface BillingService {
	Long createInvoice(InvoiceRequest request);
	FinanceReportResponse getFinanceReport();
	InvoiceResponse getByOrderId(Long orderId);
	List<InvoiceResponse> getAllInvoicesByUserId(Long userId);
}
