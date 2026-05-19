package com.modelosgr86e1eq6.proyectofacturacion.sales.repositories;

import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Sale}.
 *
 * <p>Expone consultas específicas que el módulo de facturas necesita
 * para recuperar una venta con sus relaciones eager cuando se va a
 * generar una factura, evitando múltiples roundtrips a la BD.</p>
 *
 * @author MrBraro
 */
public interface SaleRepository extends JpaRepository<Sale, Integer> {

    /**
     * Recupera una venta junto con su cliente y sus líneas de detalle
     * (incluyendo el producto de cada línea) en una sola consulta.
     *
     * <p>Usado por {@code InvoiceService} antes de invocar el Builder,
     * garantizando que el builder tenga todos los datos disponibles
     * sin disparar consultas lazy adicionales.</p>
     *
     * @param saleId identificador de la venta
     * @return {@link Optional} con la venta y sus relaciones cargadas
     */
    @Query("SELECT s FROM Sale s " +
           "JOIN FETCH s.client " +
           "LEFT JOIN FETCH s.details d " +
           "LEFT JOIN FETCH d.product " +
           "WHERE s.idSale = :saleId")
    Optional<Sale> findByIdWithDetails(@Param("saleId") Integer saleId);
}
