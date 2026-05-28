package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationStatus;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
 
/**
 * Decorador de Email.
 * Envuelve cualquier Notifier y añade el envío por correo electrónico
 * usando JavaMailSender. Actualiza el estado del registro en BD
 * a SENT o FAILED según el resultado.
 */
@Slf4j
public class EmailNotificationDecorator extends NotificationDecorator {
 
    private final JavaMailSender mailSender;
    private final String         fromAddress;
 
    public EmailNotificationDecorator(Notifier wrapped,
                                      NotificationRepository notificationRepository,
                                      JavaMailSender mailSender,
                                      String fromAddress) {
        super(wrapped, notificationRepository);
        this.mailSender  = mailSender;
        this.fromAddress = fromAddress;
    }
 
    @Override
    protected void doSend(NotificationContext context) {
        log.info("[EmailDecorator] Enviando email a: {}", context.getClientEmail());
 
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            mail.setTo(context.getClientEmail());
            mail.setSubject(context.getSubject());
            mail.setText(context.getMessage());
 
            mailSender.send(mail);
 
            updateStatus(context, NotificationStatus.SENT);
            log.info("[EmailDecorator] Email enviado exitosamente a: {}", context.getClientEmail());
 
        } catch (MailException ex) {
            log.error("[EmailDecorator] Fallo al enviar email a {}: {}", context.getClientEmail(), ex.getMessage());
            updateStatus(context, NotificationStatus.FAILED);
        }
    }
}