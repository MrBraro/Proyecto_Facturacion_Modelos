package com.modelosgr86e1eq6.proyectofacturacion.invoices.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.CreateInvoiceRequest;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.dto.InvoiceResponse;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.services.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del módulo de facturas.
 *
 * @author MrBraro
 */
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // ── RF-18 / RF-19: POST /api/v1/invoices ─────────────────────────────────
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
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(invoiceService.findAll()));
    }

    // ── GET /api/v1/invoices/{id} ─────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> findById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(ApiResponse.ok(invoiceService.findById(id)));
    }

    // ── GET /api/v1/invoices/{id}/export?format=pdf|xml|json ─────────────────
    @GetMapping("/{id}/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<byte[]> export(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "pdf") String format) {

        byte[] bytes = invoiceService.export(id, format);

        InvoiceResponse invoice = invoiceService.findById(id);
        String fileName = "invoice-" + invoice.getInvoiceNumber() + "." + format.toLowerCase();

        MediaType mediaType = switch (format.toLowerCase()) {
            case "xml"  -> MediaType.APPLICATION_XML;
            case "json" -> MediaType.APPLICATION_JSON;
            default     -> MediaType.APPLICATION_PDF;
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }
}