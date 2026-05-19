package com.modelosgr86e1eq6.proyectofacturacion.pattern.builder.invoice;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;

import java.time.LocalDate;

/**
 * Implementación del {@link InvoiceBuilder} para facturas de tipo {@code SIMPLE}.
 *
 * <h3>Responsabilidad</h3>
 * <p>Construye una factura básica con encabezado, detalles de productos y totales.
 * No incluye QR ni marca de agua. Los métodos {@link #addQr()} y
 * {@link #addWatermark()} son no-op intencionalmente: respetan el contrato
 * de la interfaz sin romperlo ni lanzar excepciones.</p>
 *
 * <h3>Datos de entrada</h3>
 * <p>Recibe la {@link Sale} ya cargada con sus relaciones en el constructor.
 * El servicio es responsable de proveer la venta con {@code client} y
 * {@code details} (con sus {@code product}) ya inicializados para evitar
 * {@code LazyInitializationException}.</p>
 *
 * <h3>No recalcula nada</h3>
 * <p>Los valores de {@code subtotal}, {@code tax} y {@code total} se copian
 * directamente desde la venta. El stock, el IVA y los precios ya fueron
 * procesados por el módulo de ventas.</p>
 *
 * <h3>Extensibilidad</h3>
 * <p>Para añadir soporte de QR a facturas simples en el futuro, basta con
 * sobreescribir {@link #addQr()} en una subclase o crear un nuevo builder
 * sin modificar esta clase ni el {@link InvoiceDirector}.</p>
 *
 * @author MrBraro
 * @see InvoiceBuilder
 * @see InvoiceDirector
 */
public class SimpleInvoiceBuilder implements InvoiceBuilder {

    /** Venta origen desde la que se construye la factura. */
    private final Sale sale;

    /**
     * Acumulador interno de la factura en construcción.
     * Se inicializa con valores por defecto y se completa paso a paso.
     */
    private final Invoice.InvoiceBuilder invoiceBuilder;

    /**
     * Crea un builder simple para la venta indicada.
     *
     * @param sale venta ya cargada con sus relaciones (client, details, products)
     */
    public SimpleInvoiceBuilder(Sale sale) {
        this.sale           = sale;
        this.invoiceBuilder = Invoice.builder();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Establece la venta origen, la fecha de emisión de hoy, el tipo
     * {@code SIMPLE} y el estado inicial {@code PENDING}.</p>
     */
    @Override
    public InvoiceBuilder addHeader() {
        invoiceBuilder
                .sale(sale)
                .issueDate(LocalDate.now())
                .type(InvoiceType.SIMPLE)
                .payStatus(InvoicePayStatus.PENDING);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Para la factura simple, los detalles de producto son accesibles
     * a través de {@code sale.getDetails()}. Esta sección queda preparada
     * para que el servicio de PDF los itere al momento de renderizar el
     * documento. No se duplica la información en la entidad Invoice.</p>
     */
    @Override
    public InvoiceBuilder addProductDetails() {
        // Product details are retrieved from sale.getDetails() during PDF rendering.
        // No duplication into Invoice entity; Sale.details already holds this data.
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Copia directamente los totales calculados por el módulo de ventas.
     * No se recalculan impuestos ni descuentos.</p>
     */
    @Override
    public InvoiceBuilder addTotal() {
        invoiceBuilder
                .subtotal(sale.getSubtotal())
                .tax(sale.getTax())
                .total(sale.getTotal());
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>No-op para factura SIMPLE.</strong> La factura simple no incluye
     * código QR. Este método existe para respetar el contrato de la interfaz
     * y permitir que el {@link InvoiceDirector} invoque el flujo completo
     * sin condicionales adicionales.</p>
     *
     * <p>Para añadir soporte QR, crear {@code QrInvoiceBuilder} que extienda
     * o reimplemente este método inyectando el servicio QR correspondiente.</p>
     */
    @Override
    public InvoiceBuilder addQr() {
        // No-op: SIMPLE invoices do not include QR codes.
        // Future: inject QrGeneratorService and call invoiceBuilder.hasQr(true).
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>No-op para factura SIMPLE.</strong> La factura simple no incluye
     * marca de agua. Este método existe únicamente para respetar el contrato
     * de la interfaz {@link InvoiceBuilder}.</p>
     *
     * <p>Para añadir soporte de watermark, crear {@code WatermarkInvoiceBuilder}
     * que reimplemente este método con el texto y la configuración de renderizado.</p>
     */
    @Override
    public InvoiceBuilder addWatermark() {
        // No-op: SIMPLE invoices do not include watermarks.
        // Future: inject WatermarkService and call invoiceBuilder.hasWatermark(true).
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return entidad {@link Invoice} de tipo SIMPLE lista para persistir
     */
    @Override
    public Invoice build() {
        return invoiceBuilder.build();
    }
}
