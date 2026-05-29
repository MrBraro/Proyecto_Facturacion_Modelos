package com.modelosgr86e1eq6.proyectofacturacion.notifications.dto;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationEvent;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
 
 
/**
 * Objeto inmutable que viaja a través de toda la cadena de decoradores.
 * Contiene todo lo necesario para que cada canal ejecute el envío
 * sin necesidad de consultar otras fuentes.
 */
@Getter
@Builder
public class NotificationContext {
 
    private final Integer           invoiceId;
    private final Integer           clientId;
    private final String         clientEmail;
    private final String         clientPhone;
    private final String         clientName;
    private final NotificationEvent event;
    private final NotificationType  type;
    private final String         subject;
    private final String         message;
}