package com.inventory.service.service.impl;

import org.springframework.stereotype.Service;

import com.inventory.service.entity.Inventory;
import com.inventory.service.entity.Product;
import com.inventory.service.repository.ProductRepository;
import com.inventory.service.request.ProductRequest;
import com.inventory.service.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	private final ProductRepository productRepository;

    @Transactional
    public Long createProduct(ProductRequest request) {
    	// DTO to Entity
        Product product = mapDtoToEntity(request);
        Inventory inventory = new Inventory();
        inventory.setCurrentStock(request.getInitialStock());
        inventory.setMinStockLevel(request.getMinStockLevel());
        
        // bi-directional linking since one to one mapped
        inventory.setProduct(product);
        product.setInventory(inventory);
        
        // inventory automatically saved due to cascade
        productRepository.save(product);
        
        return product.getId();
    }
    
    private Product mapDtoToEntity(ProductRequest request) {
    	Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        
        return product;
    }
}
