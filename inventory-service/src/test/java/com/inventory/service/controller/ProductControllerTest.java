package com.inventory.service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.service.exception.InsufficientStockException;
import com.inventory.service.request.AdvancedFilterRequest;
import com.inventory.service.request.ProductRequest;
import com.inventory.service.response.ProductResponse;
import com.inventory.service.response.ProductResponseAdmin;
import com.inventory.service.service.ProductService;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) 
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;
    private ProductRequest request;

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
    }
    
    @Test
    void addProduct_success() throws Exception {
        when(productService.createProduct(any()))
                .thenReturn(1L);
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }
    
    @Test
    void getAllProducts_success() throws Exception {
        when(productService.getAllProducts())
                .thenReturn(List.of(new ProductResponse()));
        mockMvc.perform(get("/api/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    void getAllProductsForAdmin_success() throws Exception {
        when(productService.getAllProductsForAdmin())
                .thenReturn(List.of(new ProductResponseAdmin()));
        mockMvc.perform(get("/api/products/all/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    void updateProduct_success() throws Exception {
        doNothing().when(productService)
                .updateProduct(eq(1L), any());
        mockMvc.perform(put("/api/products/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Product updated successfully"));
    }
    
    @Test
    void softDeleteProduct_success() throws Exception {
        doNothing().when(productService)
                .softDeleteProduct(1L);
        mockMvc.perform(delete("/api/products/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Product deactivated. It would no longer be available for customers"));
    }
    
    @Test
    void getById_success() throws Exception {
        ProductResponseAdmin response = new ProductResponseAdmin();
        response.setName("Phone");
        when(productService.getById(1L))
                .thenReturn(response);
        mockMvc.perform(get("/api/products/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }
    
    @Test
    void getAllByAdvancedFilter_success() throws Exception {
        AdvancedFilterRequest filter = new AdvancedFilterRequest();
        when(productService.getAllByAdvancedFilter(any()))
                .thenReturn(List.of(new ProductResponse()));
        mockMvc.perform(post("/api/products/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    void getLowStockProducts_success() throws Exception {
        AdvancedFilterRequest filter = new AdvancedFilterRequest();
        when(productService.getLowStockProducts(any()))
                .thenReturn(List.of(new ProductResponseAdmin()));
        mockMvc.perform(post("/api/products/lowstock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    @Test
    void updateStock_success() throws Exception {
        doNothing().when(productService)
                .updateStock(1L, 5);
        mockMvc.perform(patch("/api/products/update/1/stock")
                .param("quantityChange", "5"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string("Stock updated successfully"));
    }
    
    @Test
    void updateStock_insufficientStock() throws Exception {
        doThrow(new InsufficientStockException(
                "not enough stock"))
                .when(productService)
                .updateStock(1L, -100);
        mockMvc.perform(patch("/api/products/update/1/stock")
                .param("quantityChange", "-100"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content()
                        .string("not enough stock"));
    }
    
    @Test
    void addProduct_validationError() throws Exception {
        ProductRequest invalidRequest = new ProductRequest();
        mockMvc.perform(post("/api/products/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }
}
