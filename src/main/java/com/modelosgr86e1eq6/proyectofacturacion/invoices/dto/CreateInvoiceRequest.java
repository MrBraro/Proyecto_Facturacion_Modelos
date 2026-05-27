package com.modelosgr86e1eq6.proyectofacturacion.invoices.dto;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada para la generación de una nueva factura (RF-18, RF-19).
 *
 * <p>El cliente del API solo necesita indicar la venta origen y el tipo
 * de factura deseado. El resto de los datos (totales, cliente, productos)
 * son recuperados desde la venta por el servicio.</p>
 *
 * @author MrBraro
 */
@Data
public class CreateInvoiceRequest {

    /**
     * Identificador de la venta desde la que se genera la factura.
     * Debe corresponder a una venta existente sin factura previa.
     */
    @NotNull(message = "The sale ID is required")
    private Integer saleId;

    /**
     * Tipo de factura a generar.
     * <ul>
     *   <li>{@code SIMPLE}   — factura básica sin QR ni watermark.</li>
     *   <li>{@code DETAILED} — factura completa con QR y watermark.</li>
     * </ul>
     */
    @NotNull(message = "The invoice type is required")
    private InvoiceType type;
}
