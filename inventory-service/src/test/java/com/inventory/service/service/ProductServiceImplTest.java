package com.inventory.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inventory.service.entity.Inventory;
import com.inventory.service.entity.Product;
import com.inventory.service.exception.ResourceNotFoundException;
import com.inventory.service.repository.ProductRepository;
import com.inventory.service.request.AdvancedFilterRequest;
import com.inventory.service.request.ProductRequest;
import com.inventory.service.response.ProductResponse;
import com.inventory.service.response.ProductResponseAdmin;
import com.inventory.service.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;
    private ProductRequest request;
    private Product product;

    @BeforeEach
    void setup() {
        request = new ProductRequest();
        request.setName("Phone");
        request.setDescription("Smartphone");
        request.setBrand("Samsung");
        request.setCategory("Electronics");
        request.setPrice(10000.0);
        request.setDiscount(10.0);
        request.setIsActive(true);
        request.setInitialStock(50);
        request.setMinStockLevel(5);

        product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(1000.0);
        product.setDiscount(10.0);
        product.setIsActive(true);

        Inventory inventory = new Inventory();
        inventory.setCurrentStock(50);
        inventory.setMinStockLevel(5);
        inventory.setProduct(product);

        product.setInventory(inventory);
    }
    
    @Test
    void createProduct_success() {
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });
        Long id = productService.createProduct(request);
        assertEquals(1L, id);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void getAllProducts_onlyActiveReturned() {
        Product inactive = new Product();
        inactive.setIsActive(false);
        when(productRepository.findAll())
                .thenReturn(List.of(product, inactive));
        List<ProductResponse> result = productService.getAllProducts();
        assertEquals(1, result.size());
        assertEquals("Phone", result.get(0).getName());
    }
    
    @Test
    void getAllProductsForAdmin_success() {
        when(productRepository.findAll())
                .thenReturn(List.of(product));
        List<ProductResponseAdmin> result =
                productService.getAllProductsForAdmin();
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getMinStockLevel());
    }
    
    @Test
    void updateProduct_success() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        productService.updateProduct(1L, request);
        verify(productRepository).save(product);
        assertEquals(9000.0, product.getFinalPrice());
    }
    
    @Test
    void updateProduct_notFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(1L, request));
    }
    
    @Test
    void softDeleteProduct_success() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        productService.softDeleteProduct(1L);
        assertFalse(product.getIsActive());
        verify(productRepository).save(product);
    }
    
    @Test
    void getById_success() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        ProductResponseAdmin response =
                productService.getById(1L);
        assertEquals("Phone", response.getName());
    }

    @Test
    void getById_notFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> productService.getById(1L));
    }
    
    @Test
    void getAllByAdvancedFilter_success() {
        AdvancedFilterRequest filter = new AdvancedFilterRequest();
        when(productRepository.findByOptionalParams(
                any(), any(), any(), any()))
                .thenReturn(List.of(product));
        List<ProductResponse> result =
                productService.getAllByAdvancedFilter(filter);
        assertEquals(1, result.size());
    }
    
    @Test
    void getLowStockProducts_onlyLowStockReturned() {
        product.getInventory().setCurrentStock(5);
        product.getInventory().setMinStockLevel(5);
        AdvancedFilterRequest filter = new AdvancedFilterRequest();
        when(productRepository.findByOptionalParams(
                any(), any(), any(), any()))
                .thenReturn(List.of(product));
        List<ProductResponseAdmin> result =
                productService.getLowStockProducts(filter);
        assertEquals(1, result.size());
    }
    
    @Test
    void updateStock_success() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        productService.updateStock(1L, -10);
        assertEquals(40, product.getInventory().getCurrentStock());
    }
}
