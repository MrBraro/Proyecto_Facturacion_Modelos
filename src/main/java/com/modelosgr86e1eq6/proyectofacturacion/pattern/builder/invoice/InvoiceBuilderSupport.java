package com.modelosgr86e1eq6.proyectofacturacion.pattern.builder.invoice;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;

import java.time.LocalDate;

/**
 * Package-private helper class containing shared builder logic.
 *
 * <p>Prevents duplication of header and total assignment between
 * {@link SimpleInvoiceBuilder} and {@link DetailedInvoiceBuilder} without
 * resorting to fragile inheritance or violating composition principles.</p>
 *
 * @author MrBraro
 */
final class InvoiceBuilderSupport {

    private InvoiceBuilderSupport() {
        // Prevents instantiation
    }

    /**
     * Shared logic to assign header details to an invoice.
     *
     * @param builder the builder target
     * @param sale    the sale source
     * @param type    the invoice type (SIMPLE or DETAILED)
     */
    static void applyHeader(Invoice.InvoiceBuilder builder, Sale sale, InvoiceType type) {
        builder.sale(sale)
               .issueDate(LocalDate.now())
               .type(type)
               .payStatus(InvoicePayStatus.PENDING);
    }

    /**
     * Shared logic to assign total amounts to an invoice.
     *
     * @param builder the builder target
     * @param sale    the sale source
     */
    static void applyTotal(Invoice.InvoiceBuilder builder, Sale sale) {
        builder.subtotal(sale.getSubtotal())
               .tax(sale.getIva())
               .total(sale.getTotal());
    }
}
