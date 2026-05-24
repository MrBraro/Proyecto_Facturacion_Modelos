package com.modelosgr86e1eq6.proyectofacturacion.invoices;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.CreateInvoiceRequest;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.InvoiceResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.exceptions.InvoiceAlreadyExistsException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.mappers.InvoiceMapper;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories.InvoiceRepository;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.services.InvoiceService;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleRepository;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.PdfGeneratorUtil;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.watermark.StatusWatermarkStrategy;
import com.modelosgr86e1eq6.proyectofacturacion.util.qr.QrGeneratorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link InvoiceService}.
 *
 * @author MrBraro
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private QrGeneratorUtil qrGeneratorUtil;

    @Mock
    private StatusWatermarkStrategy watermarkStrategy;

    @Mock
    private PdfGeneratorUtil pdfGeneratorUtil;

    @InjectMocks
    private InvoiceService invoiceService;

    private Sale sampleSale;

    @BeforeEach
    void setUp() {
        sampleSale = Sale.builder()
                .idSale(100)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("19.00"))
                .total(new BigDecimal("119.00"))
                .build();
    }

    @Test
    void create_simpleInvoice_success() {
        // Arrange
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setSaleId(100);
        request.setType(InvoiceType.SIMPLE);

        when(saleRepository.findByIdWithDetails(100)).thenReturn(Optional.of(sampleSale));
        when(invoiceRepository.existsBySale_IdSale(100)).thenReturn(false);

        InvoiceResponse responseDto = new InvoiceResponse();
        responseDto.setId(1);
        responseDto.setInvoiceNumber("INV-2026-000001");
        responseDto.setType(InvoiceType.SIMPLE);
        responseDto.setHasQr(false);
        responseDto.setHasWatermark(false);

        when(invoiceMapper.toResponse(any(Invoice.class))).thenReturn(responseDto);

        // Act
        InvoiceResponse result = invoiceService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals(InvoiceType.SIMPLE, result.getType());
        assertFalse(result.isHasQr());
        assertFalse(result.isHasWatermark());

        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());

        Invoice savedInvoice = invoiceCaptor.getValue();
        assertEquals(sampleSale, savedInvoice.getSale());
        assertEquals(InvoiceType.SIMPLE, savedInvoice.getType());
        assertFalse(savedInvoice.isHasQr());
        assertFalse(savedInvoice.isHasWatermark());
    }

    @Test
    void create_detailedInvoice_success() {
        // Arrange
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setSaleId(100);
        request.setType(InvoiceType.DETAILED);

        when(saleRepository.findByIdWithDetails(100)).thenReturn(Optional.of(sampleSale));
        when(invoiceRepository.existsBySale_IdSale(100)).thenReturn(false);
        when(watermarkStrategy.resolveText(InvoicePayStatus.PENDING)).thenReturn("PENDING");

        InvoiceResponse responseDto = new InvoiceResponse();
        responseDto.setId(1);
        responseDto.setInvoiceNumber("INV-2026-000001");
        responseDto.setType(InvoiceType.DETAILED);
        responseDto.setHasQr(true);
        responseDto.setHasWatermark(true);
        responseDto.setWatermarkText("PENDING");

        when(invoiceMapper.toResponse(any(Invoice.class))).thenReturn(responseDto);

        // Act
        InvoiceResponse result = invoiceService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals(InvoiceType.DETAILED, result.getType());
        assertTrue(result.isHasQr());
        assertTrue(result.isHasWatermark());
        assertEquals("PENDING", result.getWatermarkText());

        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());

        Invoice savedInvoice = invoiceCaptor.getValue();
        assertEquals(sampleSale, savedInvoice.getSale());
        assertEquals(InvoiceType.DETAILED, savedInvoice.getType());
        assertTrue(savedInvoice.isHasQr());
        assertTrue(savedInvoice.isHasWatermark());
        assertEquals("PENDING", savedInvoice.getWatermarkText());
    }

    @Test
    void create_throwsIfDuplicateInvoice() {
        // Arrange
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setSaleId(100);
        request.setType(InvoiceType.SIMPLE);

        when(saleRepository.findByIdWithDetails(100)).thenReturn(Optional.of(sampleSale));
        when(invoiceRepository.existsBySale_IdSale(100)).thenReturn(true);

        // Act & Assert
        assertThrows(InvoiceAlreadyExistsException.class, () -> invoiceService.create(request));
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void create_throwsIfSaleNotFound() {
        // Arrange
        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setSaleId(999);
        request.setType(InvoiceType.SIMPLE);

        when(saleRepository.findByIdWithDetails(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> invoiceService.create(request));
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void findById_throwsIfNotFound() {
        // Arrange
        when(invoiceRepository.findByIdWithDetails(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> invoiceService.findById(999));
    }
}
