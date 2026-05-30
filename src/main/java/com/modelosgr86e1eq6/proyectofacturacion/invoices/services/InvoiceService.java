package com.modelosgr86e1eq6.proyectofacturacion.invoices.services;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder.DetailedInvoiceBuilder;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder.InvoiceBuilder;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder.InvoiceDirector;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder.SimpleInvoiceBuilder;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Strategy.StatusWatermarkStrategy;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.CreateInvoiceRequest;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.InvoiceResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.exceptions.InvoiceAlreadyExistsException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.mappers.InvoiceMapper;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories.InvoiceRepository;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Visitor.InvoiceVisitor;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Visitor.JsonInvoiceVisitor;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Visitor.PdfInvoiceVisitor;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Visitor.XmlInvoiceVisitor;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.observer.InvoiceGeneratedEvent;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleDetailRepository;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleRepository;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.PdfGeneratorUtil;
import com.modelosgr86e1eq6.proyectofacturacion.util.qr.QrGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository       invoiceRepository;
    private final SaleRepository          saleRepository;
    private final InvoiceMapper           invoiceMapper;
    private final QrGeneratorUtil         qrGeneratorUtil;
    private final StatusWatermarkStrategy watermarkStrategy;
    private final SaleDetailRepository    saleDetailRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PdfInvoiceVisitor       pdfInvoiceVisitor;
    private final XmlInvoiceVisitor       xmlInvoiceVisitor;
    private final JsonInvoiceVisitor      jsonInvoiceVisitor;

    // ── RF-18 / RF-19: Generate invoice ──────────────────────────────────────

    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request) {
        log.info("Generating invoice for sale ID: {}, type: {}", request.getSaleId(), request.getType());

        Sale sale = loadSaleOrThrow(request.getSaleId());
        validateNoDuplicateInvoice(request.getSaleId());

        InvoiceBuilder builder  = resolveBuilder(request.getType(), sale);
        InvoiceDirector director = new InvoiceDirector(builder);

        Invoice invoice = buildByType(director, sale, request.getType());
        Invoice saved   = invoiceRepository.save(invoice);

        var client = sale.getClient();
        eventPublisher.publishEvent(new InvoiceGeneratedEvent(
                this,
                saved.getIdInvoice(),
                client.getIdClient(),
                client.getName(),
                client.getEmail(),
                client.getTelephone(),
                saved.getInvoiceNumber()
        ));

        log.info("Invoice created successfully for sale ID: {}", request.getSaleId());
        return invoiceMapper.toResponse(saved,
                saleDetailRepository.findBySaleId(request.getSaleId()));
    }

    // ── List invoices ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAll() {
        return invoiceRepository.findAllWithSaleAndClient()
                .stream()
                .map(i -> invoiceMapper.toResponse(i,
                        saleDetailRepository.findBySaleId(i.getSale().getId())))
                .toList();
    }

    // ── Find invoice by ID ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public InvoiceResponse findById(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + invoiceId));
        return invoiceMapper.toResponse(invoice,
                saleDetailRepository.findBySaleId(invoice.getSale().getId()));
    }

    // ── Export via Visitor ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] export(Integer invoiceId, String format) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + invoiceId));

        InvoiceVisitor visitor = resolveVisitor(format);
        log.info("Exporting invoice {} as {}", invoiceId, format.toUpperCase());
        return invoice.accept(visitor);
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private InvoiceVisitor resolveVisitor(String format) {
        return switch (format.toLowerCase()) {
            case "pdf"  -> pdfInvoiceVisitor;
            case "xml"  -> xmlInvoiceVisitor;
            case "json" -> jsonInvoiceVisitor;
            default     -> throw new IllegalArgumentException(
                    "Unsupported export format: " + format);
        };
    }

    private Sale loadSaleOrThrow(Integer saleId) {
        return saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sale not found with id: " + saleId));
    }

    private void validateNoDuplicateInvoice(Integer saleId) {
        if (invoiceRepository.existsBySale_Id(saleId)) {
            throw new InvoiceAlreadyExistsException(saleId);
        }
    }

    private InvoiceBuilder resolveBuilder(InvoiceType type, Sale sale) {
        return switch (type) {
            case SIMPLE   -> new SimpleInvoiceBuilder(sale);
            case DETAILED -> new DetailedInvoiceBuilder(sale, qrGeneratorUtil, watermarkStrategy);
        };
    }

    private Invoice buildByType(InvoiceDirector director, Sale sale, InvoiceType type) {
        return switch (type) {
            case SIMPLE   -> director.buildSimple(sale);
            case DETAILED -> director.buildDetailed(sale);
        };
    }
}