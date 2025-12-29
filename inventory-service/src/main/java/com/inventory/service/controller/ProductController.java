package com.inventory.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.service.request.ProductRequest;
import com.inventory.service.response.ProductResponse;
import com.inventory.service.response.ProductResponseAdmin;
import com.inventory.service.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<Long> addProduct(@Valid @RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
    	ResponseEntity<List<ProductResponse>> allProducts =  
    			new ResponseEntity<>(
    			productService.getAllProducts(), 
    			HttpStatus.OK);
    	
    	return allProducts;
    }
    
    @GetMapping("/all/admin")
    public ResponseEntity<List<ProductResponseAdmin>> 
    getAllProductsForAdmin(){
    	ResponseEntity<List<ProductResponseAdmin>> allProducts =  
    			new ResponseEntity<>(
    			productService.getAllProductsForAdmin(), 
    			HttpStatus.OK);
    	
    	return allProducts;
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateProduct(
    		@PathVariable Long id, @Valid @RequestBody ProductRequest request){
    	productService.updateProduct(id, request);
    	return ResponseEntity.ok("Product updated successfully");
    }
}
