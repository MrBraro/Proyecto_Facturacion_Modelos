package com.modelosgr86e1eq6.proyectofacturacion.sales.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleStatus;

import java.time.LocalDateTime;

public interface SaleRepository extends JpaRepository<Sale, Integer> {

    @Query("""
    SELECT s FROM Sale s
    WHERE s.client.idClient = COALESCE(:clientId, s.client.idClient)
      AND s.state = COALESCE(:estado, s.state)
      AND s.saleDate >= COALESCE(:from, s.saleDate)
      AND s.saleDate <= COALESCE(:to, s.saleDate)
    """)
    Page<Sale> findByFilters(
            @Param("clientId") Integer clientId,
            @Param("estado") SaleStatus estado,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}