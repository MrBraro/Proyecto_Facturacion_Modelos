package com.modelosgr86e1eq6.proyectofacturacion;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync   // Habilita el @Async en AuditService
public class ProyectoFacturacionApplication {

    public static void main(String[] args) {
        // Cargar variables del .env ANTES de iniciar Spring
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
        
        SpringApplication.run(ProyectoFacturacionApplication.class, args);
    }
}