package com.modelosgr86e1eq6.proyectofacturacion.sales.repositories;

import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleDetailRepository extends JpaRepository<SaleDetail, Integer> {

    @Query("""
        SELECT sd FROM SaleDetail sd
        WHERE sd.sale.id = :saleId
    """)
    List<SaleDetail> findBySaleId(@Param("saleId") Integer saleId);
}