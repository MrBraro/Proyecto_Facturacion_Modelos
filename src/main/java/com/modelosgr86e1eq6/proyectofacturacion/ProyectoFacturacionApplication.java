package com.modelosgr86e1eq6.proyectofacturacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync   // Habilita el @Async en AuditService
public class ProyectoFacturacionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoFacturacionApplication.class, args);
    }
}