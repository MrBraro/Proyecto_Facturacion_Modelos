package com.modelosgr86e1eq6.proyectofacturacion.util.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * Utility component for generating QR codes using ZXing.
 *
 * <h3>QR Content Format</h3>
 * <p>The content follows a pipe-delimited key:value schema designed to be
 * extensible for future payment gateway integrations:</p>
 * <pre>
 * INV:{invoiceNumber}|TOTAL:{total}|REF:{invoiceId}|GATEWAY:PENDING
 * </pre>
 * <ul>
 *   <li>{@code INV}     — Invoice number (human-readable identifier).</li>
 *   <li>{@code TOTAL}   — Total amount to collect.</li>
 *   <li>{@code REF}     — Invoice ID (numeric PK, stable reference).</li>
 *   <li>{@code GATEWAY} — Reserved for payment gateway integration (Wompi,
 *       MercadoPago, Nequi, Daviplata). Value {@code PENDING} until integrated.</li>
 * </ul>
 *
 * <h3>Extensibility</h3>
 * <p>To integrate a real gateway, add the gateway-specific payload to the
 * content string (e.g., {@code |GATEWAY:WOMPI|REF_ID:xyz}) without breaking
 * existing QR consumers that only read the stable fields.</p>
 *
 * @author MrBraro
 */
@Slf4j
@Component
public class QrGeneratorUtil {

    private static final int DEFAULT_SIZE_PX = 200;
    private static final String FORMAT         = "PNG";

    /**
     * Generates a QR code PNG image as a byte array for the given invoice data.
     *
     * @param invoiceNumber  formatted invoice number (e.g., {@code INV-2026-000001})
     * @param invoiceId      numeric PK of the invoice (used as stable payment reference)
     * @param total          total amount to encode in the QR
     * @return PNG-encoded QR image bytes
     * @throws QrGenerationException if ZXing fails to encode or write the image
     */
    public byte[] generateForInvoice(String invoiceNumber, Integer invoiceId, BigDecimal total) {
        String content = buildQrContent(invoiceNumber, invoiceId, total);
        log.debug("Generating QR for invoice: {} with content length: {}", invoiceNumber, content.length());
        return generate(content, DEFAULT_SIZE_PX);
    }

    /**
     * Generates a QR code that encodes a payment URL for simulation.
     *
     * @param invoiceId numeric PK of the invoice
     * @param baseUrl   base URL of the application
     * @return PNG-encoded QR image bytes
     */
    public byte[] generatePaymentUrl(Integer invoiceId, String baseUrl) {
        String url = baseUrl + "/api/v1/payments/qr/pay/" + invoiceId;
        log.debug("Generating payment QR with URL: {}", url);
        return generate(url, DEFAULT_SIZE_PX);
    }

    /**
     * Generates a QR code PNG image from the given raw content string.
     *
     * @param content  the text/URL to encode in the QR code
     * @param sizePx   width and height in pixels (square)
     * @return PNG-encoded QR image bytes
     * @throws QrGenerationException if encoding or image writing fails
     */
    public byte[] generate(String content, int sizePx) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, FORMAT, out);
            return out.toByteArray();

        } catch (WriterException | IOException ex) {
            log.error("Failed to generate QR code for content: {}", content, ex);
            throw new QrGenerationException("QR generation failed: " + ex.getMessage(), ex);
        }
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    /**
     * Builds the extensible QR content string.
     * The GATEWAY field is the designated extension point for real payment
     * integrations without breaking existing QR readers that only consume
     * the INV/TOTAL/REF fields.
     */
    private String buildQrContent(String invoiceNumber, Integer invoiceId, BigDecimal total) {
        return "INV:" + invoiceNumber
             + "|TOTAL:" + total.toPlainString()
             + "|REF:" + invoiceId
             + "|GATEWAY:PENDING";
    }
}
