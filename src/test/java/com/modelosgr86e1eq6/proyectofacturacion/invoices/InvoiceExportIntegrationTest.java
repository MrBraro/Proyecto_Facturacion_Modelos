package com.modelosgr86e1eq6.proyectofacturacion.invoices;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.modelosgr86e1eq6.proyectofacturacion.clients.entities.Client;
import com.modelosgr86e1eq6.proyectofacturacion.clients.repositories.ClientRepository;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.CreateInvoiceRequest;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.InvoiceResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories.InvoiceRepository;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.services.InvoiceService;
import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductRepository;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Realistic integration tests using existing PostgreSQL database
 * to test PostgreSQL specific enums, triggers, and the complete invoice generation
 * and PDF export flow.
 *
 * @author MrBraro
 */
@SpringBootTest
class InvoiceExportIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5433/facturacion_db");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "cesr1311");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Client savedClient;
    private Product savedProduct;
    private Sale savedSale;

    @BeforeEach
    void setUp() {
        // Clean up database tables in order of dependency
        invoiceRepository.deleteAll();
        saleRepository.deleteAll();
        productRepository.deleteAll();
        clientRepository.deleteAll();

        // 1. Create the sequence and postgresql trigger for invoice numbering
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS invoice_num_seq START 1;");
        jdbcTemplate.execute("CREATE OR REPLACE FUNCTION fn_invoice_number() RETURNS TRIGGER AS $$\n" +
                "BEGIN\n" +
                "    NEW.invoice_number := 'INV-2026-' || LPAD(nextval('invoice_num_seq')::text, 6, '0');\n" +
                "    RETURN NEW;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_invoice_number ON invoices;");
        jdbcTemplate.execute("CREATE TRIGGER trg_invoice_number BEFORE INSERT ON invoices FOR EACH ROW EXECUTE FUNCTION fn_invoice_number();");

        // 2. Insert master data
        Client client = Client.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .telephone("123456789")
                .address("123 Main St, Springfield")
                .isActive(true)
                .build();
        savedClient = clientRepository.save(client);

        Product product = Product.builder()
                .code("PROD-100")
                .name("Product Deluxe A")
                .price(new BigDecimal("50.00"))
                .stock(100)
                .isActive(true)
                .build();
        savedProduct = productRepository.save(product);

        // 3. Create a transaction sale
        Sale sale = Sale.builder()
                .client(savedClient)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("19.00"))
                .total(new BigDecimal("119.00"))
                .build();

        SaleDetail detail = SaleDetail.builder()
                .sale(sale)
                .product(savedProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("50.00"))
                .lineTotal(new BigDecimal("100.00"))
                .build();

        sale.setDetails(List.of(detail));
        savedSale = saleRepository.save(sale);
    }

    @Test
    void testCompleteInvoiceGenerationAndExportFlow() throws Exception {
        // --- STEP 1: Generate a Detailed Invoice ---
        CreateInvoiceRequest createRequest = new CreateInvoiceRequest();
        createRequest.setSaleId(savedSale.getIdSale());
        createRequest.setType(InvoiceType.DETAILED);

        InvoiceResponse invoiceResponse = invoiceService.create(createRequest);

        assertNotNull(invoiceResponse);
        assertNotNull(invoiceResponse.getId());
        assertNotNull(invoiceResponse.getInvoiceNumber());
        assertTrue(invoiceResponse.getInvoiceNumber().startsWith("INV-2026-"));
        assertEquals(InvoiceType.DETAILED, invoiceResponse.getType());
        assertTrue(invoiceResponse.isHasQr());
        assertTrue(invoiceResponse.isHasWatermark());
        assertEquals("PENDING", invoiceResponse.getWatermarkText());

        // Verify it was correctly saved to the DB
        Invoice dbInvoice = invoiceRepository.findById(invoiceResponse.getId()).orElse(null);
        assertNotNull(dbInvoice);
        assertEquals(invoiceResponse.getInvoiceNumber(), dbInvoice.getInvoiceNumber());
        assertNull(dbInvoice.getPdfPath(), "PDF should not be generated yet");

        // --- STEP 2: Export PDF ---
        byte[] pdfBytes = invoiceService.exportPdf(invoiceResponse.getId());

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        // Verify PDF Header Magic Bytes (%PDF-)
        assertEquals((byte) 0x25, pdfBytes[0]); // '%'
        assertEquals((byte) 0x50, pdfBytes[1]); // 'P'
        assertEquals((byte) 0x44, pdfBytes[2]); // 'D'
        assertEquals((byte) 0x46, pdfBytes[3]); // 'F'
        assertEquals((byte) 0x2D, pdfBytes[4]); // '-'

        // Verify DB got updated with the PDF path
        Invoice dbInvoiceAfterExport = invoiceRepository.findById(invoiceResponse.getId()).orElse(null);
        assertNotNull(dbInvoiceAfterExport);
        assertNotNull(dbInvoiceAfterExport.getPdfPath());
        assertTrue(Files.exists(Paths.get(dbInvoiceAfterExport.getPdfPath())));

        // --- STEP 3: iText 8 Text Extraction & Verification ---
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)))) {
            assertEquals(1, pdfDoc.getNumberOfPages(), "Invoice should fit on a single page");

            String textContent = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
            assertNotNull(textContent);

            // Assert business details are printed
            assertTrue(textContent.contains("BILLING SYSTEM"), "Should contain company name");
            assertTrue(textContent.contains(invoiceResponse.getInvoiceNumber()), "Should contain invoice number");
            assertTrue(textContent.contains("John Doe"), "Should contain client name");
            assertTrue(textContent.contains("PROD-100"), "Should contain product code");
            assertTrue(textContent.contains("Product Deluxe A"), "Should contain product name");
            assertTrue(textContent.contains("$100.00"), "Should contain line total");
            assertTrue(textContent.contains("$19.00"), "Should contain tax");
            assertTrue(textContent.contains("$119.00"), "Should contain grand total");

            // Assert watermark is present in the text content
            assertTrue(textContent.contains("PENDING"), "Should contain the diagonal watermark text 'PENDING'");
        }

        // --- STEP 4: Test Reusability of PDF ---
        // Modify the file on disk to simulate a cached version
        String diskPath = dbInvoiceAfterExport.getPdfPath();
        byte[] customMockBytes = "MOCK-CACHED-PDF-CONTENT".getBytes();
        Files.write(Paths.get(diskPath), customMockBytes);

        // Fetch it again, it should reuse the existing file instead of regenerating
        byte[] retrievedBytes = invoiceService.exportPdf(invoiceResponse.getId());
        assertArrayEquals(customMockBytes, retrievedBytes, "Should reuse the existing PDF from disk instead of regenerating it");
    }
}