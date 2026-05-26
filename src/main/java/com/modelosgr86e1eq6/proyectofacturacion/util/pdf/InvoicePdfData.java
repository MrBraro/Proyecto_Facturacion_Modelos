package com.modelosgr86e1eq6.proyectofacturacion.util.pdf;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Immutable data carrier passed to {@link PdfGeneratorUtil}.
 *
 * <p>Decouples the PDF rendering engine from JPA entities, preventing lazy
 * initialisation exceptions and keeping {@code PdfGeneratorUtil} free of
 * Spring Data / Hibernate dependencies. Assembled by {@link
 * com.modelosgr86e1eq6.proyectofacturacion.invoices.services.InvoiceService}
 * from the already-loaded {@code Invoice} and its {@code Sale}.</p>
 *
 * @author MrBraro
 */
public record InvoicePdfData(

        // ── Invoice metadata ──────────────────────────────────────────────────
        Integer     invoiceId,
        String      invoiceNumber,
        LocalDate   issueDate,
        InvoiceType type,
        InvoicePayStatus payStatus,

        // ── Client ────────────────────────────────────────────────────────────
        String clientName,
        String clientEmail,
        String clientPhone,
        String clientAddress,

        // ── Totals ────────────────────────────────────────────────────────────
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,

        // ── Line items ────────────────────────────────────────────────────────
        List<LineItem> lineItems,

        // ── PDF options ───────────────────────────────────────────────────────
        boolean hasQr,
        boolean hasWatermark,
        String  watermarkText

) {

    /**
     * Represents a single product line in the invoice.
     *
     * @param productCode  semantic code of the product (e.g., {@code P001})
     * @param productName  descriptive name of the product
     * @param quantity     number of units sold
     * @param unitPrice    unit price at the time of sale
     * @param lineTotal    {@code quantity × unitPrice}
     */
    public record LineItem(
            String     productCode,
            String     productName,
            int        quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {}
}
