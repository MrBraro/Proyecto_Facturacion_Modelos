package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.decorator;

import com.modelosgr86e1eq6.proyectofacturacion.notifications.dto.NotificationContext;
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
 
    @Override
    public void send(NotificationContext context) {
        // Extremo de la cadena: no persiste ni envía nada.
        // La persistencia es responsabilidad de cada decorador concreto,
        // uno por canal, con su propio registro independiente.
        log.debug("[BaseNotifier] Inicio de cadena para factura: {}", context.getInvoiceId());
    }
}