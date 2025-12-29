package com.inventory.service.service;

import java.util.List;

import com.inventory.service.request.ProductRequest;
import com.inventory.service.response.ProductResponse;

public interface ProductService {
	Long createProduct(ProductRequest request);
	List<ProductResponse> getAllProducts();
}
