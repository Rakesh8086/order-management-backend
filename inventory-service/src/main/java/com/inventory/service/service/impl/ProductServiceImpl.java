package com.inventory.service.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.inventory.service.entity.Inventory;
import com.inventory.service.entity.Product;
import com.inventory.service.exception.ResourceNotFoundException;
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
    		if(!response.getIsActive()) {
    			continue;
    		}
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
    		if(!response.getIsActive()) {
    			continue;
    		}
    		allResponses.add(response);
    	}
    	
    	return allResponses;
    }
    
	@Override
	public void updateProduct(Long id, ProductRequest request) {
		Product existingProduct = productRepository.findById(id)
		        .orElseThrow(() -> 
		        new ResourceNotFoundException(
		        		"Product not found with id: " + id));
		existingProduct.setName(request.getName());
	    existingProduct.setDescription(request.getDescription());
	    existingProduct.setBrand(request.getBrand());
	    existingProduct.setCategory(request.getCategory());
	    existingProduct.setPrice(request.getPrice());
	    existingProduct.setDiscount(request.getDiscount());
	    existingProduct.setIsActive(request.getIsActive());
	    if(existingProduct.getInventory() != null) {
	        existingProduct.getInventory().setCurrentStock(request.getInitialStock());
	        existingProduct.getInventory().setMinStockLevel(request.getMinStockLevel());
	    }

	    productRepository.save(existingProduct);
	    // return "Product updated Successfully!";
	}
	
    private Product mapDtoToEntity(ProductRequest request) {
    	Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setIsActive(request.getIsActive());
        
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
    	response.setIsActive(product.getIsActive());
    	
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
    	response.setIsActive(product.getIsActive());
    	
    	return response;
    }
}
