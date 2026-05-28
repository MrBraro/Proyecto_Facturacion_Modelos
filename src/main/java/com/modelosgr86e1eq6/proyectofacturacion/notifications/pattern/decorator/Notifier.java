package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;

// ─────────────────────────────────────────────────────────────────────────────
//  INTERFAZ DEL COMPONENTE
//  Contrato que implementan tanto el componente base como todos los decoradores
// ─────────────────────────────────────────────────────────────────────────────
public interface Notifier {
    /**
     * Ejecuta el envío de la notificación para el contexto dado.
     * Cada decorador llama primero a su wrapped.send() y luego
     * añade su propio canal de envío.
     */
    void send(NotificationContext context);
}
