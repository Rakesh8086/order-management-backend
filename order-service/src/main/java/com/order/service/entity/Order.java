package com.order.service.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.Range;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Order date is required")
    @FutureOrPresent(message = "The order date cannot be in past")
    private LocalDateTime orderDate;
    @NotNull(message = "must not be null")
    @Min(value = 1, message = "Total Amount must be atleast 1")
    private Double totalAmount;
    @NotNull(message = "Status is required")
    private OrderStatus status;
    @NotNull(message = "must not be null")
    @Range(min = 1, max = 5, message = "value should be between 1 and 5")
    private Integer deliveryWithinDays;
    @NotBlank
    private String shippingAddress;
    @Valid
    @NotEmpty
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<@NotNull OrderItem> items = new ArrayList<>();
}
