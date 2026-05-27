package com.modelosgr86e1eq6.proyectofacturacion.util.pdf;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class that activates {@link PdfStorageProperties}.
 *
 * <p>Placing {@code @EnableConfigurationProperties} here (rather than on the
 * main application class) keeps the activation scoped to the storage module,
 * following the project's layered architecture pattern.</p>
 *
 * @author MrBraro
 */
@Configuration
@EnableConfigurationProperties(PdfStorageProperties.class)
public class PdfStorageConfig {
}
