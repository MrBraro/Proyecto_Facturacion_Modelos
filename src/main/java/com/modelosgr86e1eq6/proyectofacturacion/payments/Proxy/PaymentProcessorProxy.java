package com.modelosgr86e1eq6.proyectofacturacion.payments.Proxy;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import com.modelosgr86e1eq6.proyectofacturacion.invoices.repositories.InvoiceRepository;
import com.modelosgr86e1eq6.proyectofacturacion.payments.Factory.PaymentProcessor;
import com.modelosgr86e1eq6.proyectofacturacion.payments.dto.CreatePaymentRequest;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.Payment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentProcessorProxy implements PaymentProcessor {

    private final PaymentProcessor  realProcessor;
    private final InvoiceRepository invoiceRepository;

    public PaymentProcessorProxy(PaymentProcessor realProcessor,
                                 InvoiceRepository invoiceRepository) {
        this.realProcessor    = realProcessor;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Payment processPayment(CreatePaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with id: " + request.getInvoiceId()));

        if (invoice.getPayStatus() == InvoicePayStatus.PAID) {
            log.warn("Attempt to pay already paid invoice: {}", request.getInvoiceId());
            throw new BusinessException("Invoice is already paid");
        }

        log.info("Processing payment for invoice: {}", request.getInvoiceId());
        return realProcessor.processPayment(request);
    }
}