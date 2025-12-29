package com.inventory.service.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String name;
    private String description;
    private String brand;
    private String category;
    private Double price;
    private Double discount;
    private Double finalPrice;
    private int currentStock; 
    private Boolean isActive;
}
