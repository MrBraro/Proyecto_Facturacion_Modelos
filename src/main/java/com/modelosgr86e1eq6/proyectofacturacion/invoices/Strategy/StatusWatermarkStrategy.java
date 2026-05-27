package com.modelosgr86e1eq6.proyectofacturacion.invoices.Strategy;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.InvoicePayStatus;
import org.springframework.stereotype.Component;

/**
 * Watermark strategy that derives the stamp text from the invoice payment status.
 *
 * <p>This is the default strategy injected into {@code DetailedInvoiceBuilder}.
 * Mapping:</p>
 * <ul>
 *   <li>{@code PAID}     → {@code "PAID"}</li>
 *   <li>{@code PENDING}  → {@code "PENDING"}</li>
 *   <li>{@code REJECTED} → {@code "REJECTED"}</li>
 * </ul>
 *
 * @author MrBraro
 */
@Component
public class StatusWatermarkStrategy implements WatermarkStrategy {

    @Override
    public String resolveText(InvoicePayStatus status) {
        return switch (status) {
            case PAID     -> "PAID";
            case PENDING  -> "PENDING";
            case REJECTED -> "REJECTED";
        };
    }
}
