package com.modelosgr86e1eq6.proyectofacturacion.invoices.mappers;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.InvoiceLineItemResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.InvoiceResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mapper para convertir la entidad {@link Invoice} al DTO de salida {@link InvoiceResponse}.
 *
 * @author MrBraro
 */
@Component
public class InvoiceMapper {

    /**
     * Convierte una {@link Invoice} a su representación pública {@link InvoiceResponse}.
     *
     * @param invoice entidad a convertir
     * @param details lista de detalles de la venta asociada
     * @return DTO con los datos completos de la factura
     */
    public InvoiceResponse toResponse(Invoice invoice, List<SaleDetail> details) {
        InvoiceResponse response = new InvoiceResponse();

        response.setId(invoice.getIdInvoice());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setSaleId(invoice.getSale().getId());

        // Client data — denormalized from sale.client
        response.setClientName(invoice.getSale().getClient().getName());
        response.setClientEmail(invoice.getSale().getClient().getEmail());

        response.setType(invoice.getType());
        response.setPayStatus(invoice.getPayStatus());

        response.setSubtotal(invoice.getSubtotal());
        response.setTax(invoice.getTax());
        response.setTotal(invoice.getTotal());

        response.setLineItems(mapLineItems(details));

        response.setPdfPath(invoice.getPdfPath());
        response.setHasQr(invoice.isHasQr());
        response.setHasWatermark(invoice.isHasWatermark());
        response.setWatermarkText(invoice.getWatermarkText());

        response.setIssueDate(invoice.getIssueDate());
        response.setCreatedAt(invoice.getCreatedAt());

        return response;
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private List<InvoiceLineItemResponse> mapLineItems(List<SaleDetail> details) {
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }
        return details.stream()
                .map(this::mapLineItem)
                .toList();
    }

    private InvoiceLineItemResponse mapLineItem(SaleDetail detail) {
        InvoiceLineItemResponse item = new InvoiceLineItemResponse();
        item.setProductCode(detail.getProduct().getCode());
        item.setProductName(detail.getProduct().getName());
        item.setQuantity(detail.getQuantity());
        item.setUnitPrice(detail.getUnitPrice());
        item.setLineTotal(detail.getLineSubtotal());
        return item;
    }
}