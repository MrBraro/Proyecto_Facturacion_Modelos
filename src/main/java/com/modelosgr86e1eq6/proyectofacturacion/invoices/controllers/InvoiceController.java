package com.modelosgr86e1eq6.proyectofacturacion.invoices.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.CreateInvoiceRequest;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.InvoiceResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.services.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del módulo de facturas.
 *
 * <p>Expone los endpoints del API según la documentación REST existente.
 * No contiene lógica de negocio: delega completamente en {@link InvoiceService}.</p>
 *
 * <h3>Endpoints implementados</h3>
 * <ul>
 *   <li>{@code POST   /api/v1/invoices}       — RF-18/RF-19: Generar factura desde venta.</li>
 *   <li>{@code GET    /api/v1/invoices}        — Listar todas las facturas.</li>
 *   <li>{@code GET    /api/v1/invoices/{id}}   — Detalle completo de una factura.</li>
 * </ul>
 *
 * <h3>Endpoint futuro (preparado por el Builder)</h3>
 * <ul>
 *   <li>{@code GET /api/v1/invoices/{id}/export?format={format}&qr={bool}&watermark={bool}}
 *       — Exportar factura como PDF con opciones de QR y watermark.
 *       Se implementará cuando el servicio de generación PDF esté disponible.</li>
 * </ul>
 *
 * @author MrBraro
 */
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ── RF-18 / RF-19: POST /api/v1/invoices ─────────────────────────────────

    /**
     * Genera una factura a partir de una venta existente.
     *
     * <p>La numeración ({@code invoiceNumber}) es asignada automáticamente
     * por el trigger PostgreSQL {@code fn_invoice_number()}. El tipo de
     * factura ({@code SIMPLE} o {@code DETAILED}) determina qué secciones
     * construye el patrón Builder.</p>
     *
     * @param request DTO con el ID de la venta y el tipo de factura
     * @return 201 Created con la factura generada
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> create(
            @Valid @RequestBody CreateInvoiceRequest request) {

        InvoiceResponse created = invoiceService.create(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.ok("Invoice generated successfully", created));
    }

    // ── GET /api/v1/invoices ──────────────────────────────────────────────────

    /**
     * Lista todas las facturas registradas, ordenadas por fecha de creación descendente.
     *
     * @return 200 OK con la lista de facturas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> findAll() {
        List<InvoiceResponse> invoices = invoiceService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(invoices));
    }

    // ── GET /api/v1/invoices/{id} ─────────────────────────────────────────────

    /**
     * Retorna el detalle completo de una factura incluyendo líneas de productos.
     *
     * @param id PK de la factura
     * @return 200 OK con el detalle de la factura, o 404 si no existe
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> findById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(ApiResponse.ok(invoiceService.findById(id)));
    }

    // ── GET /api/v1/invoices/{id}/export — Placeholder ───────────────────────

    /**
     * Placeholder para la exportación de facturas como PDF.
     *
     * <p>Este endpoint está documentado en la API REST pero no implementado aún.
     * Cuando el servicio de generación PDF esté disponible, este método delegará
     * en un {@code InvoiceExportService} que utilizará el patrón Builder
     * (con soporte de QR y watermark) para generar el archivo.</p>
     *
     * <p>Query params planificados:</p>
     * <ul>
     *   <li>{@code format}    — formato de exportación (ej. {@code pdf}).</li>
     *   <li>{@code qr}        — incluir código QR ({@code true/false}).</li>
     *   <li>{@code watermark} — incluir marca de agua ({@code true/false}).</li>
     * </ul>
     *
     * @param id     PK de la factura a exportar
     * @param format formato de exportación (actualmente no procesado)
     * @return 501 Not Implemented hasta que el servicio PDF esté disponible
     */
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<byte[]> export(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "pdf") String format) {

        if (!"pdf".equalsIgnoreCase(format)) {
            return ResponseEntity.badRequest().build();
        }

        byte[] pdfBytes = invoiceService.exportPdf(id);
        InvoiceResponse response = invoiceService.findById(id);
        String fileName = "invoice-" + response.getInvoiceNumber() + ".pdf";

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(pdfBytes);
    }
}
