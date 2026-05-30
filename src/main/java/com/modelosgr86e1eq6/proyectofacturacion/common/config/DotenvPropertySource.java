package com.modelosgr86e1eq6.proyectofacturacion.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.core.env.PropertySource;

/**
 * PropertySource personalizado para cargar variables desde .env
 */
public class DotenvPropertySource extends PropertySource<Dotenv> {

    private final Dotenv dotenv;

    public DotenvPropertySource(String name, Dotenv dotenv) {
        super(name, dotenv);
        this.dotenv = dotenv;
    }

    @Override
    public Object getProperty(String key) {
        return dotenv.get(key);
    }
}
