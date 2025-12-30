package com.order.service.request;

import java.util.List;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
	@Valid
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;
    @NotBlank
    private String shippingAddress;
    @NotNull(message = "must not be null")
    @Range(min = 1, max = 5, message = "value should be between 1 and 5")
    private int deliveryWithinDays;
}
