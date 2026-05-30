package com.modelosgr86e1eq6.proyectofacturacion.invoices.Visitor;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;

public interface InvoiceVisitor {
    byte[] visit(Invoice invoice);
}