package com.billing.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.billing.service.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i")
    Double getTotalRevenue();
    @Query("SELECT AVG(i.totalAmount) FROM Invoice i")
    Double getAverageInvoiceValue();
    Optional<Invoice> findByOrderId(Long orderId);
    List<Invoice> findByUserId(Long userId);
}