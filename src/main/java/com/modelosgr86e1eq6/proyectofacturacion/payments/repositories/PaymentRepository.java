package com.modelosgr86e1eq6.proyectofacturacion.payments.repositories;

import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.Payment;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentMethod;
import com.modelosgr86e1eq6.proyectofacturacion.payments.entities.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByExternalReference(String externalReference);

    @Query("""
        SELECT p FROM Payment p
        WHERE (CAST(:invoiceId AS string) IS NULL OR p.invoice.idInvoice = :invoiceId)
          AND (CAST(:method    AS string) IS NULL OR p.type              = :method)
          AND (CAST(:status    AS string) IS NULL OR p.status            = :status)
    """)
    Page<Payment> findByFilters(
            @Param("invoiceId") Integer       invoiceId,
            @Param("method")    PaymentMethod method,
            @Param("status")    PaymentStatus status,
            Pageable pageable
    );

    Optional<Payment> findByInvoice_IdInvoice(Integer invoiceId);
}