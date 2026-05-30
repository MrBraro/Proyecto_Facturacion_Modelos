package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.observer;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationEvent;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationType;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.List;
 
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
 
    private final NotificationService notificationService;
 
    // ── Factura generada → EMAIL + SMS ────────────────────────────────────
    @EventListener
    @Async("notificationExecutor")
    public void onInvoiceGenerated(InvoiceGeneratedEvent event) {
        log.info("[NotificationListener] Factura generada: {}", event.getInvoiceNumber());
 
        NotificationContext context = NotificationContext.builder()
                .invoiceId(event.getInvoiceId())
                .clientId(event.getClientId())
                .clientName(event.getClientName())
                .clientEmail(event.getClientEmail())
                .clientPhone(event.getClientPhone())
                .event(NotificationEvent.INVOICE_GENERATED)
                .channels(List.of(NotificationType.SMS, NotificationType.EMAIL))
                .subject("Tu factura " + event.getInvoiceNumber() + " ha sido generada")
                .message(buildInvoiceMessage(event.getClientName(), event.getInvoiceNumber()))
                .build();
 
        notificationService.notify(context);
    }
 
    // ── Pago exitoso → EMAIL + SMS ────────────────────────────────────────
    @EventListener
    @Async("notificationExecutor")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        log.info("[NotificationListener] Pago procesado para factura: {} | Exitoso: {}",
                event.getInvoiceNumber(), event.isSuccess());
 
        NotificationEvent notifEvent;
        String subject;
        String message;
 
        if (event.isSuccess()) {
            notifEvent = NotificationEvent.PAYMENT_SUCCESS;
            subject    = "Pago confirmado – Factura " + event.getInvoiceNumber();
            message    = buildPaymentSuccessMessage(
                    event.getClientName(), event.getInvoiceNumber(), event.getPaymentMethod());
        } else {
            notifEvent = NotificationEvent.PAYMENT_REJECTED;
            subject    = "Pago rechazado – Factura " + event.getInvoiceNumber();
            message    = buildPaymentRejectedMessage(
                    event.getClientName(), event.getInvoiceNumber());
        }
 
        NotificationContext context = NotificationContext.builder()
                .invoiceId(event.getInvoiceId())
                .clientId(event.getClientId())
                .clientName(event.getClientName())
                .clientEmail(event.getClientEmail())
                .clientPhone(event.getClientPhone())
                .event(notifEvent)
                .channels(List.of(NotificationType.SMS, NotificationType.EMAIL))
                .subject(subject)
                .message(message)
                .build();
 
        notificationService.notify(context);
    }
 
    // ── Mensajes ──────────────────────────────────────────────────────────
 
    private String buildInvoiceMessage(String clientName, String invoiceNumber) {
        return String.format(
                "Hola %s, tu factura %s ha sido generada exitosamente. " +
                "Puedes consultarla desde el sistema o contactar a tu asesor.",
                clientName, invoiceNumber);
    }
 
    private String buildPaymentSuccessMessage(String name, String number, String method) {
        return String.format(
                "Hola %s, hemos recibido tu pago para la factura %s mediante %s. " +
                "Tu factura quedó marcada como PAGADA. ¡Gracias!",
                name, number, method);
    }
 
    private String buildPaymentRejectedMessage(String name, String number) {
        return String.format(
                "Hola %s, el pago para la factura %s fue rechazado. " +
                "Por favor intenta nuevamente o comunícate con soporte.",
                name, number);
    }
}