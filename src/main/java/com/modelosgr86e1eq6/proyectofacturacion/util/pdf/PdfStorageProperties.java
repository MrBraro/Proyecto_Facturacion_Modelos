package com.modelosgr86e1eq6.proyectofacturacion.util.pdf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.storage")
public class PdfStorageProperties {

    private String pdfDirectory  = "./invoices/pdf";
    private String xmlDirectory  = "./invoices/xml";
    private String jsonDirectory = "./invoices/json";
}