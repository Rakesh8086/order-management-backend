package com.inventory.service.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
	@NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String brand;
    @NotBlank
    private String category;
    @Min(value = 0, message = "price cannot be negative.")
    private Double price;
    @Min(value = 0, message = "discount cannot be negative.")
    private Double discount;
    @Min(value = 0, message = "initial stock cannot be negative.")
    private int initialStock;
    @Min(value = 0, message = "minimum stock level cannot be negative.")
    private int minStockLevel;
    @NotNull
    private Boolean isActive;
}
