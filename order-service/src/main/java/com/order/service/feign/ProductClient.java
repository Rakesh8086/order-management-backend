package com.order.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.order.service.response.ProductResponseAdmin;

@FeignClient(name = "inventory-service", url = "http://localhost:8081") 
public interface ProductClient {
    @GetMapping("/api/products/id/{id}")
    ProductResponseAdmin getById(@PathVariable Long id);

    @PatchMapping("/api/products/update/{id}/stock")
    void updateStock(@PathVariable Long id, 
    		@RequestParam Integer quantityChange);
}
