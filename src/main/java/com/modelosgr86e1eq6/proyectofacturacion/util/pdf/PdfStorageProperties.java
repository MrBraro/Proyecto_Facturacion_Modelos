package com.modelosgr86e1eq6.proyectofacturacion.util.pdf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed configuration properties for invoice PDF file storage.
 *
 * <p>Reads the {@code app.storage} prefix from {@code application.yaml}.
 * Decouples all storage path decisions from business logic — changing the
 * target directory (local → S3 mount → NFS) only requires an environment
 * variable change, not a code change.</p>
 *
 * <p>Registered via {@link PdfStorageConfig}.</p>
 *
 * @author MrBraro
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.storage")
public class PdfStorageProperties {

    /**
     * Base directory where generated invoice PDFs are stored.
     * Defaults to {@code ./invoices-pdf} relative to the working directory.
     * Override with the {@code PDF_STORAGE_DIR} environment variable.
     */
    private String pdfDirectory = "./invoices-pdf";
}
