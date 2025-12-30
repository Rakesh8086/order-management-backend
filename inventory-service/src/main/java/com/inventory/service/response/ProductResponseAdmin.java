package com.inventory.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseAdmin {
	private Long id;
	private String name;
    private String description;
    private String brand;
    private String category;
    private Double price;
    private Double discount;
    private Double finalPrice;
    private Integer currentStock; 
    private Integer minStockLevel;
    private Boolean isActive;
}
