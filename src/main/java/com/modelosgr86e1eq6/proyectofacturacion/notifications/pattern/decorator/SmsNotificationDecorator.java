package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationStatus;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories.NotificationRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
 
/**
 * Decorador de SMS.
 * Envuelve cualquier Notifier y añade el envío de mensaje de texto
 * usando la API de Twilio. Actualiza el estado del registro en BD
 * a SENT o FAILED según el resultado.
 *
 * Twilio debe estar inicializado antes de usar este decorador.
 * La inicialización se hace en TwilioConfig con Twilio.init().
 */
@Slf4j
public class SmsNotificationDecorator extends NotificationDecorator {
 
    private final String twilioFromNumber;
 
    public SmsNotificationDecorator(Notifier wrapped,
                                    NotificationRepository notificationRepository,
                                    String twilioFromNumber) {
        super(wrapped, notificationRepository);
        this.twilioFromNumber = twilioFromNumber;
    }
 
    @Override
    protected void doSend(NotificationContext context) {
        String phone = context.getClientPhone();
 
        if (phone == null || phone.isBlank()) {
            log.warn("[SmsDecorator] Cliente sin teléfono registrado, omitiendo SMS. invoiceId: {}",
                    context.getInvoiceId());
            updateStatus(context, NotificationStatus.FAILED);
            return;
        }
 
        log.info("[SmsDecorator] Enviando SMS a: {}", phone);
 
        try {
            // Twilio espera el número en formato E.164: +57XXXXXXXXXX
            Message message = Message.creator(
                            new PhoneNumber(phone),
                            new PhoneNumber(twilioFromNumber),
                            buildSmsText(context))
                    .create();
 
            log.info("[SmsDecorator] SMS enviado. SID: {}", message.getSid());
            updateStatus(context, NotificationStatus.SENT);
 
        } catch (Exception ex) {
            log.error("[SmsDecorator] Fallo al enviar SMS a {}: {}", phone, ex.getMessage());
            updateStatus(context, NotificationStatus.FAILED);
        }
    }
 
    // El SMS es más corto que el email — se recorta el mensaje
    private String buildSmsText(NotificationContext context) {
        String msg = context.getMessage();
        return msg.length() > 160 ? msg.substring(0, 157) + "..." : msg;
    }
}