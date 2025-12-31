package com.order.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
	private Long productId;
    private String productName; 
    private String brand;
    private int quantity;
    private Double unitPrice;
}
