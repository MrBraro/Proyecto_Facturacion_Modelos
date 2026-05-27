package com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;

/**
 * Interfaz del patrón Builder para la construcción de facturas.
 *
 * <h3>Responsabilidad</h3>
 * <p>Define el contrato que todas las implementaciones de builder de factura
 * deben respetar. Cada método construye una sección específica de la factura
 * y retorna {@code this} para soportar encadenamiento fluent.</p>
 *
 * <h3>Diseño y extensibilidad</h3>
 * <p>El builder está desacoplado de la capa HTTP y de la infraestructura
 * de persistencia. Opera exclusivamente con entidades de dominio ({@code Sale},
 * {@code SaleDetail}, {@code Product}) que el servicio provee ya cargadas.</p>
 *
 * <p>Futuras implementaciones pueden añadir comportamiento real en los métodos
 * {@link #addQr()} y {@link #addWatermark()} sin romper el contrato:</p>
 * <ul>
 *   <li>{@code PdfInvoiceBuilder}   — genera y persiste el archivo PDF.</li>
 *   <li>{@code QrInvoiceBuilder}    — renderiza QR con datos de la venta.</li>
 *   <li>{@code DetailedInvoiceBuilder} — factura completa con QR + watermark.</li>
 * </ul>
 *
 * <h3>Flujo de uso (coordinado por {@link InvoiceDirector})</h3>
 * <pre>{@code
 * InvoiceBuilder builder = new SimpleInvoiceBuilder(sale);
 * InvoiceDirector director = new InvoiceDirector(builder);
 * Invoice invoice = director.buildSimple();
 * }</pre>
 *
 * @author MrBraro
 * @see SimpleInvoiceBuilder
 * @see InvoiceDirector
 */
public interface InvoiceBuilder {

    /**
     * Construye el encabezado de la factura.
     *
     * <p>Establece los datos de identificación: fecha de emisión, tipo de factura
     * y estado de pago inicial ({@code PENDING}). También captura los datos
     * del cliente a través de la venta asociada.</p>
     *
     * @return esta misma instancia para encadenamiento fluent
     */
    InvoiceBuilder addHeader();

    /**
     * Construye las líneas de detalle de productos.
     *
     * <p>Itera sobre los {@code SaleDetail} de la venta para incluir
     * en la factura: código y nombre de producto, cantidad, precio unitario
     * y total de línea. La lógica de cálculo ya fue ejecutada por el módulo
     * de ventas; aquí solo se reutiliza.</p>
     *
     * @return esta misma instancia para encadenamiento fluent
     */
    InvoiceBuilder addProductDetails();

    /**
     * Establece los totales de la factura.
     *
     * <p>Reutiliza directamente {@code subtotal}, {@code tax} y {@code total}
     * desde la venta. No recalcula impuestos ni aplica descuentos.</p>
     *
     * @return esta misma instancia para encadenamiento fluent
     */
    InvoiceBuilder addTotal();

    /**
     * Añade un código QR a la factura.
     *
     * <p>Las implementaciones simples pueden dejar este método como no-op.
     * Las implementaciones avanzadas generarán el QR con los datos de la
     * venta (número de factura, total, cliente) cuando el servicio QR
     * esté disponible.</p>
     *
     * @return esta misma instancia para encadenamiento fluent
     */
    InvoiceBuilder addQr();

    /**
     * Añade una marca de agua (watermark) a la factura.
     *
     * <p>Las implementaciones simples pueden dejar este método como no-op.
     * Las implementaciones avanzadas renderizarán el texto del watermark
     * en diagonal sobre el documento PDF cuando el servicio esté disponible.</p>
     *
     * @return esta misma instancia para encadenamiento fluent
     */
    InvoiceBuilder addWatermark();

    /**
     * Finaliza la construcción y retorna la entidad {@link Invoice} completa.
     *
     * <p>El objeto retornado está listo para ser persistido por el servicio.
     * No lanza excepciones si se invoca antes de llamar a todos los métodos
     * de construcción, pero el estado resultante puede ser incompleto.</p>
     *
     * @return entidad {@link Invoice} construida
     */
    Invoice build();
}
