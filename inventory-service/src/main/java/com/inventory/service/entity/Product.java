package com.inventory.service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String name;
	@NotBlank
	private String description;
	@NotBlank
	private String category;
	@NotBlank
	private String brand;
	@NotNull(message = "must not be null")
	@Min(value = 1, message = "Price cannot be negative.")
	private Double price;
	@NotNull(message = "must not be null")
	@Min(value = 0, message = "Discount cannot be negative")
	private Double discount;
	@OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private Inventory inventory;
	@NotNull(message = "must not be null")
	private Boolean isActive;
	@NotNull(message = "must not be null")
	@Min(value = 1, message = "Final Price cannot be negative.")
	private Double finalPrice;
}
