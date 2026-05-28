package com.modelosgr86e1eq6.proyectofacturacion.payments.Proxy;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories.InvoiceRepository;
import com.modelosgr86e1eq6.proyectofacturacion.payments.Factory.PaymentProcessor;
import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.CreatePaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.Payment;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentStatus;
import com.modelosgr86e1eq6.proyectofacturacion.payments.repositories.PaymentRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentProcessorProxy implements PaymentProcessor {

    private final PaymentProcessor realProcessor;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public PaymentProcessorProxy(
            PaymentProcessor realProcessor,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository
    ) {
        this.realProcessor = realProcessor;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment processPayment(CreatePaymentRequest request) {

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + request.getInvoiceId()));

        if (invoice.getPayStatus() == InvoicePayStatus.PAID) {

            log.warn("Attempt to pay already paid invoice: {}",
                    request.getInvoiceId());

            throw new BusinessException("Invoice is already paid");
        }

        paymentRepository.findByInvoice_IdInvoice(
                request.getInvoiceId()
        ).ifPresent(existingPayment -> {

            if (existingPayment.getStatus() == PaymentStatus.PENDIENTE) {

                log.warn("Invoice already has a pending payment: {}",
                        request.getInvoiceId());

                throw new BusinessException(
                        "Invoice already has a pending payment"
                );
            }

            if (existingPayment.getStatus() == PaymentStatus.RECHAZADO) {

                log.warn("Invoice already has a rejected payment: {}",
                        request.getInvoiceId());

                throw new BusinessException(
                        "Invoice already has a rejected payment"
                );
            }
        });

        log.info("Processing payment for invoice: {}",
                request.getInvoiceId());

        return realProcessor.processPayment(request);
    }
}