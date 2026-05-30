package com.modelosgr86e1eq6.proyectofacturacion.notifications.dto;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationEvent;
import com.modelosgr86e1eq6.proyectofacturacion.notifications.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
 
/**
 * Objeto inmutable que viaja a través de toda la cadena de decoradores.
 *
 * channels reemplaza al antiguo campo "type: EMAIL_SMS".
 * Define qué canales deben activarse sin codificar combinaciones en un enum.
 * La composición de decoradores en NotificationServiceImpl
 * itera esta lista para armar la cadena correspondiente.
 *
 * Ejemplo:
 *   channels = [EMAIL, SMS]  →  Email( Sms( Base ) )
 *   channels = [EMAIL]       →  Email( Base )
 *   channels = [SMS]         →  Sms( Base )
 */
@Getter
@Builder
public class NotificationContext {
 
    private final Integer                  invoiceId;
    private final Integer                  clientId;
    private final String                 clientEmail;
    private final String                 clientPhone;
    private final String                 clientName;
    private final NotificationEvent      event;
    private final List<NotificationType> channels;   // canales individuales, sin combinaciones
    private final String                 subject;
    private final String                 message;
}