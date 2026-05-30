package com.modelosgr86e1eq6.proyectofacturacion;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableAsync   // Habilita el @Async en AuditService
public class ProyectoFacturacionApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProyectoFacturacionApplication.class);
        
        // Configuramos rutas alternativas para asegurar que encuentre el archivo .env
        Dotenv dotenv = Dotenv.configure()
                .directory("./Proyecto_Facturacion_Modelos") // Intenta buscar en la subcarpeta si abriste la raíz
                .filename(".env")
                .ignoreIfMissing()
                .load();
        
        Map<String, Object> dotenvProperties = new HashMap<>();
        dotenv.entries().forEach(entry -> dotenvProperties.put(entry.getKey(), entry.getValue()));
        
        // Si la subcarpeta no tenía el .env, intentamos cargar el de la raíz actual
        if (dotenvProperties.isEmpty()) {
            Dotenv localDotenv = Dotenv.configure().ignoreIfMissing().load();
            localDotenv.entries().forEach(entry -> dotenvProperties.put(entry.getKey(), entry.getValue()));
        }
        
        app.setDefaultProperties(dotenvProperties);
        app.run(args);
    }
}