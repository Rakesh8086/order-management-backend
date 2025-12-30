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
    @Min(value = 1, message = "price cannot be negative.")
    @NotNull(message = "must not be null")
    private Double price;
    @Min(value = 0, message = "discount cannot be negative.")
    @NotNull(message = "must not be null")
    private Double discount;
    @Min(value = 1, message = "initial stock cannot be negative.")
    @NotNull(message = "must not be null")
    private Integer initialStock;
    @Min(value = 0, message = "minimum stock level cannot be negative.")
    @NotNull(message = "must not be null")
    private Integer minStockLevel;
    @NotNull(message = "must not be null")
    private Boolean isActive;
}
