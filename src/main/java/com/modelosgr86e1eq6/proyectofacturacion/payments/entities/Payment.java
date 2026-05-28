package com.modelosgr86e1eq6.proyectofacturacion.payments.entities;

import com.modelosgr86e1eq6.proyectofacturacion.invoices.entities.Invoice;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false)
    private PaymentMethod type;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private PaymentStatus status;

    @Column(name = "monto", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "referencia_externa")
    private String externalReference;

    @Column(name = "pasarela")
    private String paymentGateway;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt   = LocalDateTime.now();
        this.paymentDate = LocalDateTime.now();
    }
}