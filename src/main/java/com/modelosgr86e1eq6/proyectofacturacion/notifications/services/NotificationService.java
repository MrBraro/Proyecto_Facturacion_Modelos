package com.modelosgr86e1eq6.proyectofacturacion.notifications.services;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
 
public interface NotificationService {
    /**
     * Ensambla la cadena de decoradores según el tipo definido en el context
     * y ejecuta el envío completo.
     */
    void notify(NotificationContext context);
}
 