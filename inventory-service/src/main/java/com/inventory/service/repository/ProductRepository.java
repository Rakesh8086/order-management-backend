package com.inventory.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventory.service.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByName(String name);
    @Query("SELECT p FROM Product p WHERE " +
    	       "(:name IS NULL OR p.name = :name) AND " +
    	       "(:brand IS NULL OR p.brand = :brand) AND " +
    	       "(:finalPrice IS NULL OR p.finalPrice <= :finalPrice) AND " +
    	       "(:discount IS NULL OR p.discount >= :discount) " + 
    	       "ORDER BY p.finalPrice ASC") 
	 List<Product> findByOptionalParams(
	     @Param("name") String name, 
	     @Param("brand") String brand, 
	     @Param("finalPrice") Double finalPrice,
	     @Param("discount") Double discount
	 );
}
