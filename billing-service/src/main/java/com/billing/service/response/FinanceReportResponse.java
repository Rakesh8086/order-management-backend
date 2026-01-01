package com.billing.service.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceReportResponse {
	private Double totalRevenue;
    private Long totalOrders;
    private Double averageOrderValue;
    private LocalDate reportGeneratedDate;
    private LocalTime reportGeneratedTime;
}
