package com.modelosgr86e1eq6.proyectofacturacion.invoices.Visitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleDetailRepository;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.InvoicePdfData;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.PdfStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonInvoiceVisitor implements InvoiceVisitor {

    private final SaleDetailRepository saleDetailRepository;
    private final PdfStorageProperties storageProperties;

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

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            byte[] bytes = mapper.writeValueAsBytes(data);
            saveToFile(invoice.getInvoiceNumber(), bytes);
            return bytes;

        } catch (Exception ex) {
            log.error("Failed to generate JSON for invoice: {}", invoice.getInvoiceNumber(), ex);
            throw new RuntimeException("JSON generation failed: " + ex.getMessage(), ex);
        }
    }

    private void saveToFile(String invoiceNumber, byte[] bytes) {
        try {
            Path dir = Paths.get(storageProperties.getJsonDirectory());
            Files.createDirectories(dir);
            Path file = dir.resolve("invoice-" + invoiceNumber + ".json");
            Files.write(file, bytes);
            log.info("JSON saved to: {}", file.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save JSON file for invoice: {}", invoiceNumber, e);
        }
    }
}