package com.modelosgr86e1eq6.proyectofacturacion.pattern.builder.invoice;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.watermark.WatermarkStrategy;
import com.modelosgr86e1eq6.proyectofacturacion.util.qr.QrGeneratorUtil;

/**
 * Concrete builder for {@code DETAILED} invoices.
 *
 * <p>Enables QR codes and watermarks using the injected {@link QrGeneratorUtil}
 * and {@link WatermarkStrategy}. Shares standard header/total setup via
 * {@link InvoiceBuilderSupport}.</p>
 *
 * @author MrBraro
 */
public class DetailedInvoiceBuilder implements InvoiceBuilder {

    private final Sale sale;
    @SuppressWarnings("unused")
    private final QrGeneratorUtil qrGeneratorUtil;
    private final WatermarkStrategy watermarkStrategy;
    private final Invoice.InvoiceBuilder invoiceBuilder;

    /**
     * Constructs a DetailedInvoiceBuilder.
     *
     * @param sale              the sale source data
     * @param qrGeneratorUtil   utility for generating QR codes
     * @param watermarkStrategy strategy for resolving watermark text
     */
    public DetailedInvoiceBuilder(Sale sale, QrGeneratorUtil qrGeneratorUtil, WatermarkStrategy watermarkStrategy) {
        this.sale = sale;
        this.qrGeneratorUtil = qrGeneratorUtil;
        this.watermarkStrategy = watermarkStrategy;
        this.invoiceBuilder = Invoice.builder();
    }

    @Override
    public InvoiceBuilder addHeader() {
        InvoiceBuilderSupport.applyHeader(invoiceBuilder, sale, InvoiceType.DETAILED);
        return this;
    }

    @Override
    public InvoiceBuilder addProductDetails() {
        // Line details are read from Sale relations at rendering time to prevent duplication.
        return this;
    }

    @Override
    public InvoiceBuilder addTotal() {
        InvoiceBuilderSupport.applyTotal(invoiceBuilder, sale);
        return this;
    }

    @Override
    public InvoiceBuilder addQr() {
        invoiceBuilder.hasQr(true);
        return this;
    }

    @Override
    public InvoiceBuilder addWatermark() {
        invoiceBuilder.hasWatermark(true);
        // New invoices default to PENDING payment status
        String text = watermarkStrategy.resolveText(InvoicePayStatus.PENDING);
        invoiceBuilder.watermarkText(text);
        return this;
    }

    @Override
    public Invoice build() {
        return invoiceBuilder.build();
    }
}
