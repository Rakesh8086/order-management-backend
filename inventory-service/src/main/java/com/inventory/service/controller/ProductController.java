package com.inventory.service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.service.request.AdvancedFilterRequest;
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
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Long> addProduct(@Valid @RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }
    
    @GetMapping("/all")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
    	ResponseEntity<List<ProductResponse>> allProducts =  
    			new ResponseEntity<>(
    			productService.getAllProducts(), 
    			HttpStatus.OK);
    	
    	return allProducts;
    }
    
    @GetMapping("/all/admin")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<ProductResponseAdmin>> 
    getAllProductsForAdmin(){
    	ResponseEntity<List<ProductResponseAdmin>> allProducts =  
    			new ResponseEntity<>(
    			productService.getAllProductsForAdmin(), 
    			HttpStatus.OK);
    	
    	return allProducts;
    }
    
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<String> updateProduct(
    		@PathVariable Long id, @Valid @RequestBody ProductRequest request){
    	productService.updateProduct(id, request);
    	return ResponseEntity.ok("Product updated successfully");
    }
    
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<String> softDeleteProduct(
    		@PathVariable Long id){
    	productService.softDeleteProduct(id);
    	return ResponseEntity.ok("Product deactivated. It would no longer "
    			+ "be available for customers");
    }
    
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
    public ResponseEntity<ProductResponseAdmin> 
    getById(@PathVariable Long id){
    	ResponseEntity<ProductResponseAdmin> Product =  
    			new ResponseEntity<>(
    			productService.getById(id), 
    			HttpStatus.OK);
    	
    	return Product;
    }
    
    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
    public ResponseEntity<List<ProductResponse>> getAllByName(
    		@PathVariable String name){
    	ResponseEntity<List<ProductResponse>> allProducts =  
    			new ResponseEntity<>(
    			productService.getAllByName(name), 
    			HttpStatus.OK);
    	
    	return allProducts;
    }
    
    @GetMapping("/filter")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('WAREHOUSE_MANAGER') or hasRole('FINANCE_OFFICER')")
    public ResponseEntity<List<ProductResponse>> getAllByAdvancedFilter(
    		@RequestBody AdvancedFilterRequest request){
    	ResponseEntity<List<ProductResponse>> allProducts =  
    			new ResponseEntity<>(
    			productService.getAllByAdvancedFilter(request), 
    			HttpStatus.OK);
    	
    	return allProducts;
    }
    
    @GetMapping("/lowStock")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<List<ProductResponseAdmin>> getLowStockProducts(
    		@RequestBody AdvancedFilterRequest request){
    	ResponseEntity<List<ProductResponseAdmin>> allProducts =  
    			new ResponseEntity<>(
    			productService.getLowStockProducts(request), 
    			HttpStatus.OK);
    	
    	return allProducts;
    }
    
    @PatchMapping("/update/{id}/stock")
    // @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<String> updateStock(
	    @PathVariable Long id, 
	    @RequestParam Integer quantityChange) { 
	    productService.updateStock(id, quantityChange);
	    
		return ResponseEntity.ok("Stock updated successfully");
	}
}
