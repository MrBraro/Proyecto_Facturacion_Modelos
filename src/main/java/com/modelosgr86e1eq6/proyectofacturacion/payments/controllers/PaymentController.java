package com.modelosgr86e1eq6.proyectofacturacion.payments.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
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
            @RequestHeader(value = "X-Signature", required = false) String signature,
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
}