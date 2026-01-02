package com.inventory.service.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProductEntityTest {
    @Test
    void product_inventory_mapping() {
        Product product = new Product();
        Inventory inventory = new Inventory();
        product.setId(1L);
        product.setName("Phone");
        product.setInventory(inventory);
        inventory.setProduct(product);
        inventory.setCurrentStock(10);

        assertEquals("Phone", product.getName());
        assertEquals(10, product.getInventory().getCurrentStock());
        assertEquals(product, inventory.getProduct());
    }
}