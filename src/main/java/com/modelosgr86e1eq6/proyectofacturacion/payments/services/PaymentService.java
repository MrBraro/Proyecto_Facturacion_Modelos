package com.modelosgr86e1eq6.proyectofacturacion.payments.services;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories.InvoiceRepository;
import com.modelosgr86e1eq6.proyectofacturacion.payments.Factory.PaymentProcessor;
import com.modelosgr86e1eq6.proyectofacturacion.payments.Factory.PaymentProcessorFactory;
import com.modelosgr86e1eq6.proyectofacturacion.payments.Proxy.PaymentProcessorProxy;
import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.CreatePaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.ManualPaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.PaymentResponse;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.Payment;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentMethod;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentStatus;
import com.modelosgr86e1eq6.proyectofacturacion.payments.mappers.PaymentMapper;
import com.modelosgr86e1eq6.proyectofacturacion.payments.repositories.PaymentRepository;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
import com.modelosgr86e1eq6.proyectofacturacion.users.repositories.UserRepository;
import com.modelosgr86e1eq6.proyectofacturacion.util.qr.QrGeneratorUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper          paymentMapper;
    private final PaymentRepository      paymentRepository;
    private final PaymentProcessorFactory factory;
    private final InvoiceRepository      invoiceRepository;
    private final UserRepository         userRepository;
    private final QrGeneratorUtil        qrGeneratorUtil;

    // ── RF-25: Process payment ────────────────────────────────────────────────

    @Transactional
    public PaymentResponse processPayment(CreatePaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + request.getInvoiceId()));

        User user = getAuthenticatedUser();

        PaymentProcessor processor = factory.getProcessor(request.getMethod());
        PaymentProcessor proxy     = new PaymentProcessorProxy(processor, invoiceRepository, paymentRepository);

        Payment payment = proxy.processPayment(request);
        payment.setInvoice(invoice);
        payment.setUser(user);
        payment.setAmount(invoice.getTotal());
        payment.setType(request.getMethod());

        if (payment.getStatus() == PaymentStatus.APROBADO) {
            invoice.setPayStatus(InvoicePayStatus.PAID);
            invoiceRepository.save(invoice);
        }

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    // ── RF-28: Manual payment ─────────────────────────────────────────────────

    @Transactional
    public PaymentResponse processManualPayment(ManualPaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + request.getInvoiceId()));

        if (invoice.getPayStatus() == InvoicePayStatus.PAID) {
            throw new BusinessException("Invoice is already paid");
        }

        User user = getAuthenticatedUser();

        Payment payment = Payment.builder()
                .invoice(invoice)
                .user(user)
                .type(PaymentMethod.CASH)
                .status(PaymentStatus.APROBADO)
                .amount(request.getAmount())
                .externalReference("MANUAL-" + UUID.randomUUID())
                .build();

        invoice.setPayStatus(InvoicePayStatus.PAID);
        invoiceRepository.save(invoice);

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    // ── RF-26: Webhook ────────────────────────────────────────────────────────

    @Value("${app.webhook.secret}")
    private String webhookSecret;

    @Transactional
    public void handleWebhook(String signature, Map<String, Object> payload) {

        if (!webhookSecret.equals(signature)) {
            log.warn("Invalid webhook signature");
            throw new BusinessException("Invalid webhook signature");
        }

        log.info("Webhook received with valid signature");

        String externalReference = (String) payload.get("reference");
        String statusRaw         = (String) payload.get("status");

        if (externalReference == null || statusRaw == null) {
            log.warn("Webhook payload missing reference or status");
            return;
        }

        paymentRepository.findByExternalReference(externalReference)
                .ifPresent(payment -> {

                    PaymentStatus newStatus =
                            "APPROVED".equalsIgnoreCase(statusRaw)
                                    ? PaymentStatus.APROBADO
                                    : PaymentStatus.RECHAZADO;

                    payment.setStatus(newStatus);

                    paymentRepository.save(payment);

                    if (newStatus == PaymentStatus.APROBADO) {

                        Invoice invoice = payment.getInvoice();

                        invoice.setPayStatus(InvoicePayStatus.PAID);

                        invoiceRepository.save(invoice);

                        log.info(
                                "Invoice {} marked as PAID via webhook",
                                invoice.getIdInvoice()
                        );
                    }
                });
    }

    // ── RF-27: Find payment by ID ─────────────────────────────────────────────

    @Transactional
    public PaymentResponse findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + id));
        return paymentMapper.toResponse(payment);
    }

    // ── RF: List payments ───────────────────────────────────────────────────

    @Transactional
    public Page<PaymentResponse> findAll(Integer invoiceId, PaymentMethod method,
                                         PaymentStatus status, Pageable pageable) {
        return paymentRepository
                .findByFilters(invoiceId, method, status, pageable)
                .map(paymentMapper::toResponse);
    }

    // ── RF-23: Generate QR ────────────────────────────────────────────────────

    public byte[] generateQr(Integer invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + invoiceId));

        return qrGeneratorUtil.generateForInvoice(
                invoice.getInvoiceNumber(),
                invoice.getIdInvoice(),
                invoice.getTotal()
        );
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found: " + email));
    }
}