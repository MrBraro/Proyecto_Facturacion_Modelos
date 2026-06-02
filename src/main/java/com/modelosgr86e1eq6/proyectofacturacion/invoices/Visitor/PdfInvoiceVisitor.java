package com.modelosgr86e1eq6.proyectofacturacion.invoices.Visitor;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleDetailRepository;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.InvoicePdfData;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.PdfGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PdfInvoiceVisitor implements InvoiceVisitor {

    private final PdfGeneratorUtil     pdfGeneratorUtil;
    private final SaleDetailRepository saleDetailRepository;

    @Override
    public byte[] visit(Invoice invoice) {
        List<SaleDetail> details = saleDetailRepository
                .findBySaleId(invoice.getSale().getId());

        List<InvoicePdfData.LineItem> items = details.stream()
                .map(d -> new InvoicePdfData.LineItem(
                        d.getProduct().getCode(),
                        d.getProduct().getName(),
                        d.getQuantity(),
                        d.getUnitPrice(),
                        d.getLineSubtotal()
                ))
                .toList();

        InvoicePdfData data = new InvoicePdfData(
                invoice.getIdInvoice(),
                invoice.getInvoiceNumber(),
                invoice.getIssueDate(),
                invoice.getType(),
                invoice.getPayStatus(),
                invoice.getSale().getClient().getName(),
                invoice.getSale().getClient().getEmail(),
                invoice.getSale().getClient().getTelephone(),
                invoice.getSale().getClient().getAddress(),
                invoice.getSubtotal(),
                invoice.getTax(),
                invoice.getTotal(),
                items,
                invoice.isHasQr(),
                invoice.isHasWatermark(),
                invoice.getWatermarkText()
        );

        return pdfGeneratorUtil.generateInvoicePdf(data);
    }
}