package com.order.service.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {
	@NotNull
    private Long orderId;
	@NotNull
    private Long userId;
	@NotNull
	@Min(value = 0, message = "Total Amount cannot be negative")
    private Double totalAmount;
}
