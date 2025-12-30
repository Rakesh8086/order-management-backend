package com.order.service.request;

import java.util.List;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;
    @NotBlank
    private String shippingAddress;
    @NotNull(message = "must not be null")
    @Range(min = 1, max = 5, message = "value should be between 1 and 5")
    private int deliveryWithinDays;
}
