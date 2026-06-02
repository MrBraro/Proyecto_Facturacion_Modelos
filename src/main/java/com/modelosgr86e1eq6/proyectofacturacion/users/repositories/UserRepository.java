package com.modelosgr86e1eq6.proyectofacturacion.users.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.modelosgr86e1eq6.proyectofacturacion.users.entities.Role;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
 
    Optional<User> findByEmail(String email);
 
    boolean existsByEmail(String email);
 
    // Cuenta cuántos ADMINs activos hay — usado para proteger el único ADMIN
    long countByRoleAndIsActiveTrue(Role role);

    /*
     * JPQL con filtros opcionales — patrón IS NULL como cortocircuito.
     * Si role llega null, la condición (:role IS NULL) es true y se ignora.
     * Mismo patrón que AuditRepository — consistente en todo el proyecto.
     */
    @Query("""
    SELECT u FROM User u
    WHERE (CAST(:role AS string) IS NULL OR u.role = :role)
""")
    Page<User> findByFilters(
            @Param("role") Role role,
            Pageable pageable
    );
}