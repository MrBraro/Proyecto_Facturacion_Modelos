package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.entities.Notification;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationStatus;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// ─────────────────────────────────────────────────────────────────────────────
//  COMPONENTE CONCRETO BASE
//  Persiste el registro de notificación en BD con estado PENDING.
//  No envía por ningún canal. Es el núcleo que todos los decoradores envuelven.
// ─────────────────────────────────────────────────────────────────────────────
@RequiredArgsConstructor
@Slf4j
public class BaseNotifier implements Notifier {
 
    private final NotificationRepository notificationRepository;
 
    @Override
    public void send(NotificationContext context) {
        log.debug("[BaseNotifier] Persistiendo notificación para factura: {}", context.getInvoiceId());
 
        Notification notification = Notification.builder()
                .invoiceId(context.getInvoiceId())
                .clientId(context.getClientId())
                .type(context.getType())
                .event(context.getEvent())
                .recipient(resolveRecipient(context))
                .subject(context.getSubject())
                .message(context.getMessage())
                .status(NotificationStatus.PENDING)
                .build();
 
        notificationRepository.save(notification);
    }
 
    // Para EMAIL_SMS se guarda email como recipient principal;
    // el decorador SMS usa clientPhone directamente del context
    private String resolveRecipient(NotificationContext context) {
        return context.getClientEmail() != null
                ? context.getClientEmail()
                : context.getClientPhone();
    }
}