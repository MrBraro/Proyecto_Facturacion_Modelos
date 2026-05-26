package com.modelosgr86e1eq6.proyectofacturacion.pattern.builder.invoice;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;

/**
 * Director del patrón Builder para la construcción de facturas.
 *
 * <h3>Responsabilidad</h3>
 * <p>Coordina el orden en que el {@link InvoiceBuilder} construye cada
 * sección de la factura. El director no conoce los detalles internos de
 * cada builder; solo sabe qué secciones incluir según el tipo de factura.</p>
 *
 * <h3>Reglas de construcción</h3>
 * <ul>
 *   <li>{@link #buildSimple(Sale)} — encabezado + productos + totales.
 *       Sin QR ni watermark.</li>
 *   <li>{@link #buildDetailed(Sale)} — todas las secciones, incluyendo
 *       QR y watermark. El builder concreto decide si los implementa
 *       o los trata como no-op.</li>
 * </ul>
 *
 * <h3>Desacoplamiento</h3>
 * <p>El director opera sobre la abstracción {@link InvoiceBuilder}, no sobre
 * implementaciones concretas. Agregar un nuevo tipo de factura (ej. exportación
 * al SRI, factura electrónica) solo requiere:</p>
 * <ol>
 *   <li>Crear una nueva implementación de {@link InvoiceBuilder}.</li>
 *   <li>Añadir un nuevo método {@code buildXxx(Sale)} al director, o crear
 *       un nuevo director si la lógica de orquestación cambia significativamente.</li>
 * </ol>
 *
 * <h3>Extensibilidad planificada</h3>
 * <p>El flujo {@code buildDetailed} está preparado para integrar:</p>
 * <ul>
 *   <li><b>QR generation</b> — al implementar {@code addQr()} en un builder concreto.</li>
 *   <li><b>Watermark rendering</b> — al implementar {@code addWatermark()} con texto configurable.</li>
 *   <li><b>PDF export</b> — un {@code PdfInvoiceBuilder} que serializa el resultado a archivo.</li>
 *   <li><b>Email notification</b> — el servicio puede enviar el PDF tras llamar a {@code build()}.</li>
 * </ul>
 *
 * <h3>Ejemplo de uso</h3>
 * <pre>{@code
 * // En InvoiceService:
 * InvoiceBuilder builder = new SimpleInvoiceBuilder(sale);
 * InvoiceDirector director = new InvoiceDirector(builder);
 * Invoice invoice = director.buildSimple(sale);
 * invoiceRepository.save(invoice);
 * }</pre>
 *
 * @author MrBraro
 * @see InvoiceBuilder
 * @see SimpleInvoiceBuilder
 */
public class InvoiceDirector {

    /** Builder concreto que será orquestado por este director. */
    private final InvoiceBuilder builder;

    /**
     * Crea el director con el builder que utilizará para construir la factura.
     *
     * @param builder implementación concreta de {@link InvoiceBuilder}
     */
    public InvoiceDirector(InvoiceBuilder builder) {
        this.builder = builder;
    }

    /**
     * Construye una factura de tipo {@code SIMPLE}.
     *
     * <p>Incluye: encabezado, detalles de productos y totales.
     * No invoca {@code addQr()} ni {@code addWatermark()}, por lo que
     * la factura resultante tendrá {@code hasQr = false} y
     * {@code hasWatermark = false}.</p>
     *
     * @param sale venta ya cargada con sus relaciones (cliente, detalles, productos)
     * @return entidad {@link Invoice} de tipo SIMPLE lista para persistir
     */
    public Invoice buildSimple(Sale sale) {
        return builder
                .addHeader()
                .addProductDetails()
                .addTotal()
                .build();
    }

    /**
     * Construye una factura de tipo {@code DETAILED}.
     *
     * <p>Incluye todas las secciones: encabezado, detalles de productos,
     * totales, QR y watermark. El comportamiento real de QR y watermark
     * depende de la implementación concreta del builder inyectado:
     * si el builder es {@link SimpleInvoiceBuilder}, ambos métodos
     * son no-op hasta que se provea una implementación avanzada.</p>
     *
     * @param sale venta ya cargada con sus relaciones (cliente, detalles, productos)
     * @return entidad {@link Invoice} de tipo DETAILED lista para persistir
     */
    public Invoice buildDetailed(Sale sale) {
        return builder
                .addHeader()
                .addProductDetails()
                .addTotal()
                .addQr()
                .addWatermark()
                .build();
    }
}
