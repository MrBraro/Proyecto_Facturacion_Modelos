package com.modelosgr86e1eq6.proyectofacturacion.util.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.modelosgr86e1eq6.proyectofacturacion.util.qr.QrGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for generating beautifully styled, premium-looking PDFs using iText 8.
 *
 * <p>Responsibility: Render a PDF document containing headers, customer metadata,
 * product list details, subtotal/tax/totals summaries, and optionally an
 * embedded payment QR code and diagonal background watermark stamp.</p>
 *
 * @author MrBraro
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfGeneratorUtil {

    private final QrGeneratorUtil qrGeneratorUtil;
    private final PdfStorageProperties pdfStorageProperties;

    /**
     * Generates a PDF as a byte array from the provided invoice PDF data.
     *
     * @param data immutable PDF data carrier
     * @return generated PDF bytes
     * @throws PdfGenerationException if PDF generation fails
     */
    public byte[] generateInvoicePdf(InvoicePdfData data) {
        log.info("Generating PDF for invoice: {}", data.invoiceNumber());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            // Modern, Slate-based Harmonious Color Palette
            DeviceRgb headerBg = new DeviceRgb(30, 41, 59); // Slate 800 (Premium dark header)
            DeviceRgb textMuted = new DeviceRgb(100, 116, 139); // Slate 500 (Muted gray)

            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // ── 1. HEADER SECTION ─────────────────────────────────────────────
            Table headerTable = new Table(new float[]{3, 2});
            headerTable.useAllAvailableWidth();

            Cell companyCell = new Cell()
                    .add(new Paragraph("BILLING SYSTEM")
                            .setFont(boldFont)
                            .setFontSize(20)
                            .setFontColor(headerBg))
                    .add(new Paragraph("Premium Invoicing Services")
                            .setFont(regularFont)
                            .setFontSize(10)
                            .setFontColor(textMuted))
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);

            Cell invoiceMetaCell = new Cell()
                    .add(new Paragraph("INVOICE")
                            .setFont(boldFont)
                            .setFontSize(22)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setFontColor(headerBg))
                    .add(new Paragraph("Number: " + data.invoiceNumber())
                            .setFont(boldFont)
                            .setFontSize(11)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Date: " + data.issueDate())
                            .setFont(regularFont)
                            .setFontSize(10)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Type: " + data.type())
                            .setFont(regularFont)
                            .setFontSize(10)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);

            headerTable.addCell(companyCell);
            headerTable.addCell(invoiceMetaCell);
            doc.add(headerTable);

            // Spacer
            doc.add(new Paragraph("\n"));

            // ── 2. CLIENT SECTION ─────────────────────────────────────────────
            Table clientTable = new Table(new float[]{1});
            clientTable.useAllAvailableWidth();
            Cell clientTitleCell = new Cell()
                    .add(new Paragraph("CLIENT INFO")
                            .setFont(boldFont)
                            .setFontSize(10)
                            .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(headerBg)
                    .setPadding(6);
            clientTable.addCell(clientTitleCell);

            Cell clientInfoCell = new Cell()
                    .add(new Paragraph("Name: " + data.clientName()).setFont(regularFont).setFontSize(9))
                    .add(new Paragraph("Email: " + data.clientEmail()).setFont(regularFont).setFontSize(9))
                    .add(new Paragraph("Phone: " + data.clientPhone()).setFont(regularFont).setFontSize(9))
                    .add(new Paragraph("Address: " + data.clientAddress()).setFont(regularFont).setFontSize(9))
                    .setPadding(8);
            clientTable.addCell(clientInfoCell);
            doc.add(clientTable);

            // Spacer
            doc.add(new Paragraph("\n"));

            // ── 3. PRODUCTS TABLE SECTION ─────────────────────────────────────
            Table productTable = new Table(new float[]{2, 4, 1, 2, 2});
            productTable.useAllAvailableWidth();

            productTable.addHeaderCell(new Cell().add(new Paragraph("Code").setFont(boldFont).setFontSize(9).setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg).setPadding(5));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Product").setFont(boldFont).setFontSize(9).setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg).setPadding(5));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Qty").setFont(boldFont).setFontSize(9).setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg).setPadding(5).setTextAlignment(TextAlignment.RIGHT));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Price").setFont(boldFont).setFontSize(9).setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg).setPadding(5).setTextAlignment(TextAlignment.RIGHT));
            productTable.addHeaderCell(new Cell().add(new Paragraph("Total").setFont(boldFont).setFontSize(9).setFontColor(ColorConstants.WHITE)).setBackgroundColor(headerBg).setPadding(5).setTextAlignment(TextAlignment.RIGHT));

            for (InvoicePdfData.LineItem item : data.lineItems()) {
                productTable.addCell(new Cell().add(new Paragraph(item.productCode()).setFont(regularFont).setFontSize(9)).setPadding(5));
                productTable.addCell(new Cell().add(new Paragraph(item.productName()).setFont(regularFont).setFontSize(9)).setPadding(5));
                productTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.quantity())).setFont(regularFont).setFontSize(9)).setPadding(5).setTextAlignment(TextAlignment.RIGHT));
                productTable.addCell(new Cell().add(new Paragraph("$" + item.unitPrice().toPlainString()).setFont(regularFont).setFontSize(9)).setPadding(5).setTextAlignment(TextAlignment.RIGHT));
                productTable.addCell(new Cell().add(new Paragraph("$" + item.lineTotal().toPlainString()).setFont(regularFont).setFontSize(9)).setPadding(5).setTextAlignment(TextAlignment.RIGHT));
            }
            doc.add(productTable);

            // Spacer
            doc.add(new Paragraph("\n"));

            // ── 4. TOTALS & QR SECTION ────────────────────────────────────────
            Table footerTable = new Table(new float[]{3, 2});
            footerTable.useAllAvailableWidth();

            Cell qrCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            if (data.hasQr()) {
                try {
                    byte[] qrBytes = qrGeneratorUtil.generateForInvoice(data.invoiceNumber(), data.invoiceId(), data.total());
                    Image qrImage = new Image(ImageDataFactory.create(qrBytes));
                    qrImage.setWidth(100);
                    qrImage.setHeight(100);
                    qrCell.add(new Paragraph("Scan to Pay / Verify").setFont(boldFont).setFontSize(9).setFontColor(headerBg));
                    qrCell.add(qrImage);
                } catch (Exception e) {
                    log.error("Failed to render QR in PDF", e);
                    qrCell.add(new Paragraph("QR Code unavailable").setFont(regularFont).setFontSize(8).setFontColor(ColorConstants.RED));
                }
            }
            footerTable.addCell(qrCell);

            Table totalsSubTable = new Table(new float[]{1, 1});
            totalsSubTable.useAllAvailableWidth();
            totalsSubTable.addCell(new Cell().add(new Paragraph("Subtotal:").setFont(regularFont).setFontSize(9)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            totalsSubTable.addCell(new Cell().add(new Paragraph("$" + data.subtotal().toPlainString()).setFont(regularFont).setFontSize(9).setTextAlignment(TextAlignment.RIGHT)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));

            totalsSubTable.addCell(new Cell().add(new Paragraph("Tax (19%):").setFont(regularFont).setFontSize(9)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
            totalsSubTable.addCell(new Cell().add(new Paragraph("$" + data.tax().toPlainString()).setFont(regularFont).setFontSize(9).setTextAlignment(TextAlignment.RIGHT)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));

            Cell totalLabelCell = new Cell().add(new Paragraph("Total:").setFont(boldFont).setFontSize(11).setFontColor(headerBg)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            Cell totalValueCell = new Cell().add(new Paragraph("$" + data.total().toPlainString()).setFont(boldFont).setFontSize(11).setFontColor(headerBg).setTextAlignment(TextAlignment.RIGHT)).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            totalsSubTable.addCell(totalLabelCell);
            totalsSubTable.addCell(totalValueCell);

            Cell totalsCell = new Cell().add(totalsSubTable).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            footerTable.addCell(totalsCell);

            doc.add(footerTable);

            // ── 5. WATERMARK STAMPING (DIAGONAL) ──────────────────────────────
            if (data.hasWatermark() && data.watermarkText() != null && !data.watermarkText().isBlank()) {
                doc.flush(); // ensure pages are created and flushed to get access to page count
                int numberOfPages = pdfDoc.getNumberOfPages();
                for (int i = 1; i <= numberOfPages; i++) {
                    PdfPage page = pdfDoc.getPage(i);
                    Paragraph p = new Paragraph(data.watermarkText())
                            .setFont(boldFont)
                            .setFontSize(60)
                            .setFontColor(ColorConstants.LIGHT_GRAY)
                            .setOpacity(0.12f); // subtle transparency watermark

                    doc.showTextAligned(p, page.getPageSize().getWidth() / 2, page.getPageSize().getHeight() / 2, i,
                            TextAlignment.CENTER, VerticalAlignment.MIDDLE, (float) Math.toRadians(45));
                }
            }

            doc.close();
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate PDF for invoice: {}", data.invoiceNumber(), e);
            throw new PdfGenerationException("PDF generation failed for invoice: " + data.invoiceNumber(), e);
        }
    }

    /**
     * Writes the given PDF bytes to local file storage.
     *
     * @param invoiceNumber the invoice number (used in the filename)
     * @param pdfBytes      the PDF document contents
     * @return target file path on disk
     * @throws PdfGenerationException if saving files fails
     */
    public String savePdfToStorage(String invoiceNumber, byte[] pdfBytes) {
        try {
            String dirPath = pdfStorageProperties.getPdfDirectory();
            Path path = Paths.get(dirPath).toAbsolutePath().normalize();
            Files.createDirectories(path);

            Path filePath = path.resolve("invoice-" + invoiceNumber + ".pdf");
            Files.write(filePath, pdfBytes);
            log.info("PDF saved to disk: {}", filePath);
            return filePath.toString();
        } catch (IOException e) {
            log.error("Failed to save PDF for invoice: {}", invoiceNumber, e);
            throw new PdfGenerationException("Failed to save PDF to storage: " + e.getMessage(), e);
        }
    }
}
