package com.modelosgr86e1eq6.proyectofacturacion.notifications.services;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator.BaseNotifier;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator.EmailNotificationDecorator;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator.Notifier;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator.SmsNotificationDecorator;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
 
/**
 * Único lugar del sistema donde se ensambla la cadena de decoradores.
 *
 * La cadena se construye de adentro hacia afuera:
 *   EMAIL_SMS : Email( Sms( Base ) )
 *   EMAIL     : Email( Base )
 *   SMS       : Sms( Base )
 *
 * Al llamar chain.send(context), la ejecución fluye:
 *   EmailDecorator.send() → SmsDecorator.send() → BaseNotifier.send()
 *   (persiste) → doSend() SMS → doSend() Email
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
 
    private final NotificationRepository notificationRepository;
    private final JavaMailSender         mailSender;
 
    @Value("${spring.mail.username}")
    private String fromAddress;
 
    @Value("${twilio.phone-number}")
    private String twilioFromNumber;
 
    @Override
    public void notify(NotificationContext context) {
        log.info("[NotificationService] Procesando notificación. Evento: {} | Tipo: {}",
                context.getEvent(), context.getType());
 
        Notifier chain = buildChain(context);
        chain.send(context);
    }
 
    // ── Construcción de la cadena de decoradores ──────────────────────────
    private Notifier buildChain(NotificationContext context) {
        Notifier base = new BaseNotifier(notificationRepository);
 
        return switch (context.getType()) {
            case EMAIL     -> wrapWithEmail(base);
            case SMS       -> wrapWithSms(base);
            case EMAIL_SMS -> wrapWithEmail(wrapWithSms(base));
        };
    }
 
    private Notifier wrapWithEmail(Notifier inner) {
        return new EmailNotificationDecorator(
                inner, notificationRepository, mailSender, fromAddress);
    }
 
    private Notifier wrapWithSms(Notifier inner) {
        return new SmsNotificationDecorator(
                inner, notificationRepository, twilioFromNumber);
    }
}