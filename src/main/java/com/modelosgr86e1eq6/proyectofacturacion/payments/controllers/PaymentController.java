package com.modelosgr86e1eq6.proyectofacturacion.payments.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories.InvoiceRepository;
import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.CreatePaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.ManualPaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.PaymentResponse;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentMethod;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentStatus;
import com.modelosgr86e1eq6.proyectofacturacion.payments.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceRepository invoiceRepository;

    // ── RF-25: Process payment ────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment processed successfully", response));
    }

    // ── RF-26: Webhook ────────────────────────────────────────────────────────
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestHeader("X-Signature") String signature,
            @RequestBody Map<String, Object> payload) {

        paymentService.handleWebhook(signature, payload);

        return ResponseEntity.ok().build();
    }

    // ── RF-27: Get payment by ID ──────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PaymentResponse>> findById(
            @PathVariable Long id) {

        PaymentResponse response = paymentService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── RF-28: Manual payment ─────────────────────────────────────────────────
    @PostMapping("/manual")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PaymentResponse>> manualPayment(
            @Valid @RequestBody ManualPaymentRequest request) {

        PaymentResponse response = paymentService.processManualPayment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Manual payment registered successfully", response));
    }

    // ── RF-?: List payments ───────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> findAll(
            @RequestParam(required = false) Integer invoiceId,
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault() Pageable pageable) {

        Page<PaymentResponse> page = paymentService.findAll(invoiceId, method, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    // ── RF-23: QR Code ────────────────────────────────────────────────────────
    @GetMapping("/qr/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<byte[]> getQr(
            @PathVariable Integer invoiceId) {

        byte[] qrBytes = paymentService.generateQr(invoiceId);
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(qrBytes);
    }

    // ── RF-23: Pay by QR simulation ──────────────────────────────────────────
    @GetMapping("/qr/pay/{invoiceId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> payByQr(
            @PathVariable Integer invoiceId) {

        PaymentResponse response = paymentService.payByQr(invoiceId);
        return ResponseEntity.ok(ApiResponse.ok("Payment completed successfully via QR", response));
    }

    @GetMapping("/qr/view/{invoiceId}")
    public ResponseEntity<String> viewQr(@PathVariable Integer invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + invoiceId));

        String html = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Pago QR - %s</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        min-height: 100vh;
                        margin: 0;
                        background-color: #f5f5f5;
                    }
                    .card {
                        background: white;
                        padding: 40px;
                        border-radius: 12px;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                        text-align: center;
                        max-width: 400px;
                        width: 100%%;
                    }
                    h2 { color: #333; margin-bottom: 8px; }
                    p  { color: #666; margin: 4px 0; }
                    .total {
                        font-size: 1.5rem;
                        font-weight: bold;
                        color: #2e7d32;
                        margin: 16px 0;
                    }
                    img {
                        width: 220px;
                        height: 220px;
                        border: 2px solid #eee;
                        border-radius: 8px;
                        margin: 16px 0;
                    }
                    .status {
                        margin-top: 16px;
                        padding: 8px 16px;
                        border-radius: 20px;
                        font-size: 0.85rem;
                        font-weight: bold;
                        background-color: #fff3e0;
                        color: #e65100;
                    }
                    .instructions {
                        font-size: 0.85rem;
                        color: #999;
                        margin-top: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <h2>Pago de Factura</h2>
                    <p><strong>%s</strong></p>
                    <p>Cliente: %s</p>
                    <div class="total">$ %s</div>
                    <img src="/api/v1/payments/qr/%d" alt="QR de pago" />
                    <div class="status">Estado: %s</div>
                    <p class="instructions">Escanea el código QR con tu celular para completar el pago</p>
                </div>
            </body>
            </html>
            """.formatted(
                org.springframework.web.util.HtmlUtils.htmlEscape(invoice.getInvoiceNumber()),
                org.springframework.web.util.HtmlUtils.htmlEscape(invoice.getInvoiceNumber()),
                org.springframework.web.util.HtmlUtils.htmlEscape(invoice.getSale().getClient().getName()),
                invoice.getTotal().toPlainString(),
                invoiceId,
                org.springframework.web.util.HtmlUtils.htmlEscape(invoice.getPayStatus().name())
        );

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }
}