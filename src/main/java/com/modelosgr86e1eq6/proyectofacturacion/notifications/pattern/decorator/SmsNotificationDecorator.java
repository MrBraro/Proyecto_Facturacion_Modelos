package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories.NotificationRepository;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.entities.Notification;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationType;
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
            log.warn("[SmsDecorator] Cliente sin teléfono, omitiendo SMS. invoiceId: {}",
                    context.getInvoiceId());
            return;
        }
 
        // Registro propio con type=SMS — completamente independiente
        // del registro EMAIL que pudo haber creado el EmailDecorator
        Notification record = createPendingRecord(
                context, NotificationType.SMS, phone);
 
        log.info("[SmsDecorator] Enviando SMS a: {}", phone);
 
        try {
            Message message = Message.creator(
                            new PhoneNumber(phone),
                            new PhoneNumber(twilioFromNumber),
                            truncate(context.getMessage()))
                    .create();
 
            markSent(record);
            log.info("[SmsDecorator] SMS enviado. SID: {}", message.getSid());
 
        } catch (Exception ex) {
            markFailed(record);
            log.error("[SmsDecorator] Fallo al enviar SMS a {}: {}", phone, ex.getMessage());
        }
    }
 
    private String truncate(String msg) {
        return msg.length() > 160 ? msg.substring(0, 157) + "..." : msg;
    }
}
 