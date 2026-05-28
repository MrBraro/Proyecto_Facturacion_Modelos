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
 
/**
 * Observer del módulo de notificaciones.
 *
 * Escucha eventos publicados por otros módulos (Invoice, Payment) mediante
 * el ApplicationEventPublisher de Spring. Al recibirlos, construye el
 * NotificationContext correspondiente y delega al NotificationService,
 * que ensambla la cadena de decoradores y ejecuta el envío.
 *
 * @Async garantiza que el procesamiento de notificaciones no bloquee
 * el hilo principal del módulo publicador.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
 
    private final NotificationService notificationService;
 
    // ── Escucha: Factura generada ─────────────────────────────────────────
    @EventListener
    @Async
    public void onInvoiceGenerated(InvoiceGeneratedEvent event) {
        log.info("[NotificationListener] Factura generada recibida: {}", event.getInvoiceNumber());
 
        String subject = "Tu factura " + event.getInvoiceNumber() + " ha sido generada";
        String message = buildInvoiceMessage(event.getClientName(), event.getInvoiceNumber());
 
        NotificationContext context = NotificationContext.builder()
                .invoiceId(event.getInvoiceId())
                .clientId(event.getClientId())
                .clientName(event.getClientName())
                .clientEmail(event.getClientEmail())
                .clientPhone(event.getClientPhone())
                .event(NotificationEvent.INVOICE_GENERATED)
                .type(NotificationType.EMAIL_SMS)
                .subject(subject)
                .message(message)
                .build();
 
        notificationService.notify(context);
    }
 
    // ── Escucha: Pago procesado (exitoso o rechazado) ─────────────────────
    @EventListener
    @Async
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
                .type(NotificationType.EMAIL_SMS)
                .subject(subject)
                .message(message)
                .build();
 
        notificationService.notify(context);
    }
 
    // ── Helpers: construcción de mensajes ────────────────────────────────
 
    private String buildInvoiceMessage(String clientName, String invoiceNumber) {
        return String.format(
                "Hola %s, tu factura con número %s ha sido generada exitosamente. " +
                "Puedes consultarla desde el sistema o contactar a tu asesor.",
                clientName, invoiceNumber);
    }
 
    private String buildPaymentSuccessMessage(String clientName,
                                               String invoiceNumber,
                                               String paymentMethod) {
        return String.format(
                "Hola %s, hemos recibido tu pago para la factura %s mediante %s. " +
                "Tu factura quedó marcada como PAGADA. ¡Gracias!",
                clientName, invoiceNumber, paymentMethod);
    }
 
    private String buildPaymentRejectedMessage(String clientName, String invoiceNumber) {
        return String.format(
                "Hola %s, lamentablemente el pago para la factura %s fue rechazado. " +
                "Por favor intenta nuevamente o comunícate con soporte.",
                clientName, invoiceNumber);
    }
}
 