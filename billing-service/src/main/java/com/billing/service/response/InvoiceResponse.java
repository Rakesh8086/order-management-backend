package com.billing.service.response;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
	@NotNull
    private Long id;
    @NotNull
    private Long orderId;      
    @NotNull
    @Min(value = 0, message = "Total Amount cannot be negative")
    private Double totalAmount; 
    @NotNull
    private LocalDate invoiceDate; 
    @NotNull
    private LocalTime invoiceTime; 
}
