package com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Invoice}.
 *
 * <p>Todas las consultas que requieran los datos del cliente o los detalles
 * de producto usan {@code JOIN FETCH} para evitar el problema N+1 típico
 * de relaciones lazy en entidades con múltiples asociaciones.</p>
 *
 * @author MrBraro
 */
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    /**
     * Verifica si ya existe una factura asociada a la venta indicada.
     * Usado en {@code InvoiceService} para garantizar la restricción 1:1
     * a nivel de aplicación antes de intentar la inserción.
     *
     * @param saleId identificador de la venta
     * @return {@code true} si ya existe una factura para esa venta
     */
    boolean existsBySale_Id(Integer saleId);

    /**
     * Recupera una factura por su ID junto con la venta, el cliente
     * y los detalles de producto en una sola consulta.
     *
     * @param invoiceId identificador de la factura
     * @return {@link Optional} con la factura completamente cargada
     */
    @Query("SELECT i FROM Invoice i " +
            "JOIN FETCH i.sale s " +
            "JOIN FETCH s.client " +
            "WHERE i.idInvoice = :invoiceId")
    Optional<Invoice> findByIdWithDetails(@Param("invoiceId") Integer invoiceId);
    /**
     * Lista todas las facturas con sus ventas y clientes cargados.
     * Usado por el endpoint de listado para evitar múltiples queries lazy.
     *
     * @return lista de facturas con relaciones básicas cargadas
     */
    @Query("SELECT i FROM Invoice i " +
           "JOIN FETCH i.sale s " +
           "JOIN FETCH s.client " +
           "ORDER BY i.createdAt DESC")
    List<Invoice> findAllWithSaleAndClient();
}
