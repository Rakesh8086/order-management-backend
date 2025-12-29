package com.inventory.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedFilterRequest {
	private String name;
	private String brand;
	private Double finalPrice;
	private Double discount;
}
