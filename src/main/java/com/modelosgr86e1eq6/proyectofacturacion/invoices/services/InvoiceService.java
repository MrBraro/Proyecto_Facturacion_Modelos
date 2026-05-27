package com.modelosgr86e1eq6.proyectofacturacion.invoices.services;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.CreateInvoiceRequest;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.InvoiceResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoiceType;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.exceptions.InvoiceAlreadyExistsException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.mappers.InvoiceMapper;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories.InvoiceRepository;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder.InvoiceBuilder;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder.InvoiceDirector;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder.SimpleInvoiceBuilder;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleDetailRepository;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleRepository;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Builder.DetailedInvoiceBuilder;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.InvoicePdfData;
import com.modelosgr86e1eq6.proyectofacturacion.util.pdf.PdfGeneratorUtil;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.Strategy.StatusWatermarkStrategy;
import com.modelosgr86e1eq6.proyectofacturacion.util.qr.QrGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.util.List;

/**
 * Servicio de negocio para la gestión del módulo de facturas.
 *
 * <h3>Responsabilidades</h3>
 * <ul>
 *   <li>RF-18: Generar una factura de tipo {@code SIMPLE} desde una venta.</li>
 *   <li>RF-19: Generar una factura de tipo {@code DETAILED} desde una venta.</li>
 *   <li>Listar facturas y recuperar el detalle completo de una factura.</li>
 * </ul>
 *
 * <h3>Integración con el patrón Builder</h3>
 * <p>El servicio actúa como cliente del patrón Builder. Instancia el builder
 * correcto según el tipo solicitado y delega la orquestación al
 * {@link InvoiceDirector}. El resultado es la entidad {@link Invoice}
 * lista para persistir.</p>
 *
 * <h3>No duplica lógica de ventas</h3>
 * <p>Los valores de {@code subtotal}, {@code tax} y {@code total} se reutilizan
 * directamente desde la {@link Sale}. El stock y el IVA ya fueron calculados
 * y descontados por el módulo de ventas.</p>
 *
 * @author MrBraro
 * @see InvoiceDirector
 * @see SimpleInvoiceBuilder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SaleRepository    saleRepository;
    private final InvoiceMapper     invoiceMapper;
    private final QrGeneratorUtil   qrGeneratorUtil;
    private final StatusWatermarkStrategy watermarkStrategy;
    private final PdfGeneratorUtil  pdfGeneratorUtil;
    private final SaleDetailRepository saleDetailRepository;

    // ── RF-18 / RF-19: Generate invoice ──────────────────────────────────────

    /**
     * Genera una nueva factura a partir de una venta existente.
     *
     * <p>Flujo:</p>
     * <ol>
     *   <li>Carga la venta con todas sus relaciones (client, details, products).</li>
     *   <li>Valida que la venta no tenga ya una factura asociada.</li>
     *   <li>Selecciona el builder adecuado según el tipo de factura.</li>
     *   <li>El director orquesta la construcción de la factura.</li>
     *   <li>Persiste la entidad; PostgreSQL asigna el número de factura vía trigger.</li>
     * </ol>
     *
     * @param request DTO con el ID de la venta y el tipo de factura deseado
     * @return DTO con la factura creada, incluyendo el número asignado por la BD
     * @throws ResourceNotFoundException     si la venta no existe
     * @throws InvoiceAlreadyExistsException si ya existe una factura para esa venta
     */
    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request) {
        log.info("Generating invoice for sale ID: {}, type: {}", request.getSaleId(), request.getType());

        Sale sale = loadSaleOrThrow(request.getSaleId());
        validateNoDuplicateInvoice(request.getSaleId());

        InvoiceBuilder builder = resolveBuilder(request.getType(), sale);
        InvoiceDirector director = new InvoiceDirector(builder);

        Invoice invoice = buildByType(director, sale, request.getType());
        invoiceRepository.save(invoice);

        log.info("Invoice created successfully for sale ID: {}", request.getSaleId());
        return invoiceMapper.toResponse(invoice, saleDetailRepository.findBySaleId(request.getSaleId()));
    }

    // ── List invoices ─────────────────────────────────────────────────────────

    /**
     * Retorna todas las facturas registradas, ordenadas por fecha de creación descendente.
     *
     * @return lista de facturas con datos básicos de cliente
     */
    @Transactional(readOnly = true)
    public List<InvoiceResponse> findAll() {
        return invoiceRepository.findAllWithSaleAndClient()
                .stream()
                .map(i -> invoiceMapper.toResponse(i, saleDetailRepository.findBySaleId(i.getSale().getId())))
                .toList();
}
    // ── Find invoice by ID ────────────────────────────────────────────────────

    /**
     * Retorna el detalle completo de una factura incluyendo todas las líneas
     * de productos.
     *
     * @param invoiceId PK de la factura
     * @return DTO con el detalle completo de la factura
     * @throws ResourceNotFoundException si la factura no existe
     */
    @Transactional(readOnly = true)
    public InvoiceResponse findById(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + invoiceId));
            return invoiceMapper.toResponse(invoice, saleDetailRepository.findBySaleId(invoice.getSale().getId()));
    }

    // ── Export PDF ──────────────────────────────────────────────────────────

    /**
     * Genera o recupera el documento PDF de una factura.
     *
     * <p>Reutiliza un PDF previamente generado en disco si está disponible,
     * optimizando el uso de CPU y almacenamiento.</p>
     *
     * @param invoiceId PK de la factura
     * @return arreglo de bytes del archivo PDF
     * @throws ResourceNotFoundException si la factura no existe
     */
    @Transactional
    public byte[] exportPdf(Integer invoiceId) {
        log.info("Exporting PDF for invoice ID: {}", invoiceId);
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + invoiceId));

        // 1. Reutilizar PDF si ya existe en disco
        if (invoice.getPdfPath() != null) {
            Path existingPath = Paths.get(invoice.getPdfPath());
            if (Files.exists(existingPath)) {
                try {
                    log.info("Reusing existing PDF from path: {}", invoice.getPdfPath());
                    return Files.readAllBytes(existingPath);
                } catch (java.io.IOException e) {
                    log.warn("Failed to read existing PDF at {}, regenerating...", invoice.getPdfPath(), e);
                }
            }
        }

        // 2. Si no existe, construir InvoicePdfData
        Sale sale = invoice.getSale();
        List<SaleDetail> details = saleDetailRepository.findBySaleId(sale.getId());

        List<InvoicePdfData.LineItem> items = details.stream()
                .map(detail -> new InvoicePdfData.LineItem(
                        detail.getProduct().getCode(),
                        detail.getProduct().getName(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getLineSubtotal()
                ))
                .toList();

        InvoicePdfData pdfData = new InvoicePdfData(
                invoice.getIdInvoice(),
                invoice.getInvoiceNumber(),
                invoice.getIssueDate(),
                invoice.getType(),
                invoice.getPayStatus(),
                sale.getClient().getName(),
                sale.getClient().getEmail(),
                sale.getClient().getTelephone(),
                sale.getClient().getAddress(),
                invoice.getSubtotal(),
                invoice.getTax(),
                invoice.getTotal(),
                items,
                invoice.isHasQr(),
                invoice.isHasWatermark(),
                invoice.getWatermarkText()
        );

        // 3. Generar PDF
        byte[] pdfBytes = pdfGeneratorUtil.generateInvoicePdf(pdfData);

        // 4. Guardar en disco
        String savedPath = pdfGeneratorUtil.savePdfToStorage(invoice.getInvoiceNumber(), pdfBytes);

        // 5. Actualizar entidad
        invoice.setPdfPath(savedPath);
        invoiceRepository.save(invoice);

        return pdfBytes;
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

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


    /**
     * Selecciona el builder concreto según el tipo de factura solicitado.
     */
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
