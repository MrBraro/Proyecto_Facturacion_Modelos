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
 * <p>Proyecta los datos del cliente (desnormalizados desde {@code Sale.client})
 * y las líneas de detalle de productos en una respuesta plana y segura para
 * exposición por la API REST.</p>
 *
 * @author MrBraro
 * @see Invoice
 * @see InvoiceResponse
 */
@Component
public class InvoiceMapper {

    /**
     * Convierte una {@link Invoice} a su representación pública {@link InvoiceResponse}.
     *
     * <p>Requiere que las relaciones {@code sale.client} y {@code sale.details}
     * estén ya inicializadas (no lazy) para evitar {@code LazyInitializationException}.
     * El repositorio provee estas relaciones mediante JOIN FETCH.</p>
     *
     * @param invoice entidad a convertir; no debe ser {@code null}
     * @return DTO con los datos completos de la factura
     */
    public InvoiceResponse toResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();

        response.setId(invoice.getIdInvoice());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setSaleId(invoice.getSale().getIdSale());

        // Client data — denormalized from sale.client
        response.setClientName(invoice.getSale().getClient().getName());
        response.setClientEmail(invoice.getSale().getClient().getEmail());

        response.setType(invoice.getType());
        response.setPayStatus(invoice.getPayStatus());

        response.setSubtotal(invoice.getSubtotal());
        response.setTax(invoice.getTax());
        response.setTotal(invoice.getTotal());

        response.setLineItems(mapLineItems(invoice.getSale().getDetails()));

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
        item.setLineTotal(detail.getLineTotal());
        return item;
    }
}
