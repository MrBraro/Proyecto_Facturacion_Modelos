package com.modelosgr86e1eq6.proyectofacturacion.util;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.modelosgr86e1eq6.proyectofacturacion.util.qr.QrGeneratorUtil;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QrGeneratorUtil}.
 *
 * @author MrBraro
 */
class QrGeneratorUtilTest {

    private final QrGeneratorUtil qrGeneratorUtil = new QrGeneratorUtil();

    @Test
    void testGenerateForInvoiceSuccess() throws Exception {
        // Arrange
        String invoiceNumber = "INV-2026-000001";
        Integer invoiceId = 42;
        BigDecimal total = new BigDecimal("1234.56");

        // Act
        byte[] qrBytes = qrGeneratorUtil.generateForInvoice(invoiceNumber, invoiceId, total);

        // Assert
        assertNotNull(qrBytes);
        assertTrue(qrBytes.length > 0);

        // Verify PNG magic bytes (89 50 4E 47 0D 0A 1A 0A)
        assertEquals((byte) 0x89, qrBytes[0]);
        assertEquals((byte) 0x50, qrBytes[1]); // 'P'
        assertEquals((byte) 0x4E, qrBytes[2]); // 'N'
        assertEquals((byte) 0x47, qrBytes[3]); // 'G'

        // Decode the QR and verify content
        ByteArrayInputStream bais = new ByteArrayInputStream(qrBytes);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(ImageIO.read(bais))));
        Result qrResult = new MultiFormatReader().decode(binaryBitmap);

        String text = qrResult.getText();
        assertNotNull(text);
        assertTrue(text.contains("INV:INV-2026-000001"), "Content: " + text);
        assertTrue(text.contains("TOTAL:1234.56"), "Content: " + text);
        assertTrue(text.contains("REF:42"), "Content: " + text);
        assertTrue(text.contains("GATEWAY:PENDING"), "Content: " + text);
    }

    @Test
    void testGenerateRawContentSuccess() {
        // Act
        byte[] qrBytes = qrGeneratorUtil.generate("Hello Antigravity!", 200);

        // Assert
        assertNotNull(qrBytes);
        assertTrue(qrBytes.length > 0);
        assertEquals((byte) 0x89, qrBytes[0]);
        assertEquals((byte) 0x50, qrBytes[1]);
        assertEquals((byte) 0x4E, qrBytes[2]);
        assertEquals((byte) 0x47, qrBytes[3]);
    }
}
