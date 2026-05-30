package com.modelosgr86e1eq6.proyectofacturacion.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

/**
 * Factory para crear PropertySource desde .env usando spring @PropertySource
 */
public class DotenvPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        
        return new DotenvPropertySource(".env", dotenv);
    }
}
