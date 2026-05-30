package com.modelosgr86e1eq6.proyectofacturacion.invoices.Singleton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class InvoiceNumberGenerator {

    private static InvoiceNumberGenerator instance;
    private final AtomicLong counter;

    private InvoiceNumberGenerator() {
        this.counter = new AtomicLong(1);
    }

    public static synchronized InvoiceNumberGenerator getInstance() {
        if (instance == null) {
            instance = new InvoiceNumberGenerator();
        }
        return instance;
    }

    public String generate() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long number = counter.getAndIncrement();
        return String.format("INV-%s-%06d", year, number);
    }
}