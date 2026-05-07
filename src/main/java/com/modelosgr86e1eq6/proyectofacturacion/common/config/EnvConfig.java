
package com.modelosgr86e1eq6.proyectofacturacion.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para cargar variables de entorno desde .env
 * 
 * Se ejecuta al iniciar la aplicación y carga todas las variables
 * del archivo .env al contexto de Spring usando System.setProperty()
 */
@Configuration
public class EnvConfig {

    static {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()  // Si .env no existe, no fallar
                .load();
        
        // Cargar variables del .env al System properties
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
