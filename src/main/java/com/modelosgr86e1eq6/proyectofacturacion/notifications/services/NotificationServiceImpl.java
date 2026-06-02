package com.modelosgr86e1eq6.proyectofacturacion.notifications.services;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator.BaseNotifier;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator.EmailNotificationDecorator;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator.Notifier;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator.SmsNotificationDecorator;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories.NotificationRepository;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
 

/**
 * Único lugar donde se ensambla la cadena de decoradores.
 *
 * Itera context.getChannels() para envolver el BaseNotifier con los
 * decoradores que correspondan. El orden en la lista determina el orden
 * de ejecución (último en la lista = más externo = se ejecuta primero).
 *
 * Agregar un nuevo canal en el futuro solo requiere:
 *   1. Añadir el valor al enum NotificationType
 *   2. Crear el nuevo decorador concreto
 *   3. Añadir un case aquí
 *   Sin tocar ninguna otra clase.
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
        log.info("[NotificationService] Evento: {} | Canales: {}",
                context.getEvent(), context.getChannels());
 
        Notifier chain = buildChain(context);
        chain.send(context);
    }
 
    private Notifier buildChain(NotificationContext context) {
        Notifier chain = new BaseNotifier();
 
        for (NotificationType channel : context.getChannels()) {
            chain = switch (channel) {
                case EMAIL -> new EmailNotificationDecorator(
                        chain, notificationRepository, mailSender, fromAddress);
                case SMS   -> new SmsNotificationDecorator(
                        chain, notificationRepository, twilioFromNumber);
            };
        }
 
        return chain;
    }
}