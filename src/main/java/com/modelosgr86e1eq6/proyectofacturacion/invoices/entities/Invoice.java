package com.modelosgr86e1eq6.proyectofacturacion.invoices.entities;

import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una factura generada a partir de una venta.
 *
 * <h3>Diseño de la relación Sale → Invoice</h3>
 * <p>La relación es estrictamente 1:1 ({@code UNIQUE} en {@code id_sale}).
 * {@code Invoice} es el lado propietario de la relación porque contiene
 * {@code @JoinColumn}. {@code Sale} navega de forma bidireccional mediante
 * {@code mappedBy = "sale"} sin asumir responsabilidad de persistencia.</p>
 *
 * <h3>Numeración automática</h3>
 * <p>El campo {@code invoiceNumber} es generado por el trigger PostgreSQL
 * {@code fn_invoice_number()} con el formato {@code INV-2026-000001}.
 * Se declara con {@code insertable = false, updatable = false} y se anota
 * con {@code @Generated(INSERT)} para que Hibernate realice un SELECT
 * inmediatamente después del INSERT y recupere el valor asignado por la BD.</p>
 *
 * <h3>Extensibilidad Builder</h3>
 * <p>Los campos {@code hasQr}, {@code hasWatermark} y {@code watermarkText}
 * son poblados por el patrón Builder ({@code SimpleInvoiceBuilder} /
 * futuros builders). El campo {@code pdfPath} será completado por el
 * servicio de generación PDF cuando esté implementado.</p>
 *
 * @author MrBraro
 * @see com.modelosgr86e1eq6.proyectofacturacion.pattern.builder.invoice.InvoiceBuilder
 * @see com.modelosgr86e1eq6.proyectofacturacion.pattern.builder.invoice.InvoiceDirector
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    /**
     * Identificador único de la factura (PK autogenerada).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invoice")
    private Integer idInvoice;

    /**
     * Número de factura generado automáticamente por el trigger PostgreSQL
     * {@code fn_invoice_number()} en formato {@code INV-YYYY-NNNNNN}.
     *
     * <p>No se inserta ni actualiza desde Java; Hibernate lo lee de la BD
     * tras el INSERT gracias a {@code @Generated(INSERT)}.</p>
     */
    @Generated(GenerationTime.INSERT)
    @Column(name = "invoice_number", insertable = false, updatable = false, length = 20)
    private String invoiceNumber;

    /**
     * Venta origen de la factura.
     * La restricción {@code UNIQUE} en {@code id_sale} garantiza 1:1 a nivel de BD.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sale", nullable = false, unique = true)
    private Sale sale;

    /**
     * Fecha de emisión de la factura.
     * Se establece en el momento de la creación mediante {@code @PrePersist}.
     */
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    /**
     * Tipo de factura: {@code SIMPLE} o {@code DETAILED}.
     * Determinado por el {@code InvoiceDirector} al construir la factura.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private InvoiceType type;

    /**
     * Estado de pago de la factura.
     * Valor inicial: {@code PENDING}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status", nullable = false, length = 20)
    @Builder.Default
    private InvoicePayStatus payStatus = InvoicePayStatus.PENDING;

    /**
     * Subtotal de la factura (antes de impuestos).
     * Reutilizado directamente desde {@link Sale#getSubtotal()}.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    /**
     * Monto de impuestos de la factura.
     * Reutilizado directamente desde {@link Sale#getTax()}.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tax;

    /**
     * Total final de la factura ({@code subtotal + tax}).
     * Reutilizado directamente desde {@link Sale#getTotal()}.
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    /**
     * Ruta del archivo PDF generado para esta factura.
     * {@code null} hasta que el servicio de generación PDF sea implementado.
     */
    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    /**
     * Indica si la factura incluye un código QR.
     * Establecido por el builder en función del tipo de factura.
     */
    @Column(name = "has_qr", nullable = false)
    @Builder.Default
    private boolean hasQr = false;

    /**
     * Indica si la factura incluye marca de agua (watermark).
     * Establecido por el builder en función del tipo de factura.
     */
    @Column(name = "has_watermark", nullable = false)
    @Builder.Default
    private boolean hasWatermark = false;

    /**
     * Texto de la marca de agua. Solo relevante cuando {@code hasWatermark = true}.
     * {@code null} para facturas sin watermark.
     */
    @Column(name = "watermark_text", length = 200)
    private String watermarkText;

    /** Fecha y hora de creación del registro en BD. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.issueDate == null) {
            this.issueDate = LocalDate.now();
        }
    }
}
