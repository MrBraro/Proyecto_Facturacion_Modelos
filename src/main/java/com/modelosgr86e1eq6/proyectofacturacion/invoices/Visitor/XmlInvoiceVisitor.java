package com.modelosgr86e1eq6.proyectofacturacion.invoices.Visitor;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleDetailRepository;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.PdfStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class XmlInvoiceVisitor implements InvoiceVisitor {

    private final SaleDetailRepository saleDetailRepository;
    private final PdfStorageProperties storageProperties;

    @Override
    public byte[] visit(Invoice invoice) {
        List<SaleDetail> details = saleDetailRepository
                .findBySaleId(invoice.getSale().getId());

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<invoice>\n");

        xml.append("  <metadata>\n");
        xml.append("    <id>").append(invoice.getIdInvoice()).append("</id>\n");
        xml.append("    <number>").append(invoice.getInvoiceNumber()).append("</number>\n");
        xml.append("    <issueDate>").append(invoice.getIssueDate()).append("</issueDate>\n");
        xml.append("    <type>").append(invoice.getType()).append("</type>\n");
        xml.append("    <payStatus>").append(invoice.getPayStatus()).append("</payStatus>\n");
        xml.append("  </metadata>\n");

        xml.append("  <client>\n");
        xml.append("    <name>").append(invoice.getSale().getClient().getName()).append("</name>\n");
        xml.append("    <email>").append(invoice.getSale().getClient().getEmail()).append("</email>\n");
        xml.append("    <phone>").append(nullSafe(invoice.getSale().getClient().getTelephone())).append("</phone>\n");
        xml.append("    <address>").append(nullSafe(invoice.getSale().getClient().getAddress())).append("</address>\n");
        xml.append("  </client>\n");

        xml.append("  <lineItems>\n");
        for (SaleDetail d : details) {
            xml.append("    <item>\n");
            xml.append("      <code>").append(d.getProduct().getCode()).append("</code>\n");
            xml.append("      <name>").append(d.getProduct().getName()).append("</name>\n");
            xml.append("      <quantity>").append(d.getQuantity()).append("</quantity>\n");
            xml.append("      <unitPrice>").append(d.getUnitPrice()).append("</unitPrice>\n");
            xml.append("      <lineTotal>").append(d.getLineSubtotal()).append("</lineTotal>\n");
            xml.append("    </item>\n");
        }
        xml.append("  </lineItems>\n");

        xml.append("  <totals>\n");
        xml.append("    <subtotal>").append(invoice.getSubtotal()).append("</subtotal>\n");
        xml.append("    <tax>").append(invoice.getTax()).append("</tax>\n");
        xml.append("    <total>").append(invoice.getTotal()).append("</total>\n");
        xml.append("  </totals>\n");

        xml.append("</invoice>");

        byte[] bytes = xml.toString().getBytes(StandardCharsets.UTF_8);
        saveToFile(invoice.getInvoiceNumber(), bytes);
        return bytes;
    }

    private void saveToFile(String invoiceNumber, byte[] bytes) {
        try {
            Path dir = Paths.get(storageProperties.getXmlDirectory());
            Files.createDirectories(dir);
            Path file = dir.resolve("invoice-" + invoiceNumber + ".xml");
            Files.write(file, bytes);
            log.info("XML saved to: {}", file.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save XML file for invoice: {}", invoiceNumber, e);
        }
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}