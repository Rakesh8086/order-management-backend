package com.inventory.service.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.inventory.service.entity.Inventory;
import com.inventory.service.entity.Product;
import com.inventory.service.repository.ProductRepository;
import com.inventory.service.request.ProductRequest;
import com.inventory.service.response.ProductResponse;
import com.inventory.service.response.ProductResponseAdmin;
import com.inventory.service.service.ProductService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	private final ProductRepository productRepository;

	@Override
    @Transactional
    public Long createProduct(ProductRequest request){
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
    
	@Override
    public List<ProductResponse> getAllProducts(){
    	List<Product> allProducts = productRepository.findAll();
    	List<ProductResponse> allResponses = new ArrayList<>();
    	for(Product prod: allProducts) {
    		ProductResponse response = mapEntityToDto(prod);
    		allResponses.add(response);
    	}
    	
    	return allResponses;
    }
	
	@Override
    public List<ProductResponseAdmin> getAllProductsForAdmin(){
    	List<Product> allProducts = productRepository.findAll();
    	List<ProductResponseAdmin> allResponses = new ArrayList<>();
    	for(Product prod: allProducts) {
    		ProductResponseAdmin response = 
    				mapEntityToDtoForAdmin(prod);
    		allResponses.add(response);
    	}
    	
    	return allResponses;
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
    
    private ProductResponse mapEntityToDto(Product product) {
    	ProductResponse response = new ProductResponse();
    	response.setName(product.getName());
    	response.setDescription(product.getDescription());
    	response.setBrand(product.getBrand());
    	response.setCategory(product.getCategory());
    	response.setPrice(product.getPrice());
    	response.setDiscount(product.getDiscount());
    	Double finalPrice = product.getPrice() - 
    			(product.getPrice()/100) * product.getDiscount();
    	response.setFinalPrice(finalPrice);
    	if(product.getInventory() != null) {
            response.setCurrentStock(product.getInventory().getCurrentStock());
        }
    	
    	return response;
    }
    
    private ProductResponseAdmin mapEntityToDtoForAdmin(Product product) {
    	ProductResponseAdmin response = new ProductResponseAdmin();
    	response.setName(product.getName());
    	response.setDescription(product.getDescription());
    	response.setBrand(product.getBrand());
    	response.setCategory(product.getCategory());
    	response.setPrice(product.getPrice());
    	response.setDiscount(product.getDiscount());
    	Double finalPrice = product.getPrice() - 
    			(product.getPrice()/100) * product.getDiscount();
    	response.setFinalPrice(finalPrice);
    	response.setId(product.getId());
    	if(product.getInventory() != null) {
    		response.setMinStockLevel(product.getInventory().getMinStockLevel());
    		response.setCurrentStock(product.getInventory().getCurrentStock());
        }
    	
    	return response;
    }
}
