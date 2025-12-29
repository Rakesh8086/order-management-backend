package com.inventory.service.service;

import java.util.List;

import com.inventory.service.request.ProductRequest;
import com.inventory.service.response.ProductResponse;
import com.inventory.service.response.ProductResponseAdmin;

public interface ProductService {
	Long createProduct(ProductRequest request);
	List<ProductResponse> getAllProducts();
	List<ProductResponseAdmin> getAllProductsForAdmin();
	void updateProduct(Long id, ProductRequest request);
	void softDeleteProduct(Long id);
	ProductResponseAdmin getById(Long id);
}
