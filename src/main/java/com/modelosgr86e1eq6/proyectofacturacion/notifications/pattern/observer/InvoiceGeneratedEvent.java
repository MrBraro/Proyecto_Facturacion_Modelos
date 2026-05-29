package com.modelosgr86e1eq6.proyectofacturacion.notifications.pattern.observer;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InvoiceGeneratedEvent extends ApplicationEvent {

    private final Integer invoiceId;
    private final Integer clientId;
    private final String clientName;
    private final String clientEmail;
    private final String clientPhone;
    private final String invoiceNumber;

    public InvoiceGeneratedEvent(Object source,
                                 Integer invoiceId,
                                 Integer clientId,
                                 String clientName,
                                 String clientEmail,
                                 String clientPhone,
                                 String invoiceNumber) {
        super(source);
        this.invoiceId = invoiceId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.invoiceNumber = invoiceNumber;
    }
}
