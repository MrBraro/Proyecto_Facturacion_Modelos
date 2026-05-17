package com.modelosgr86e1eq6.proyectofacturacion.clients.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.modelosgr86e1eq6.proyectofacturacion.clients.entities.Client;
import java.util.Optional;

 
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
 
    // Buscar solo clientes activos (usado en listados generales)
    Page<Client> findAllByIsActiveTrue(Pageable pageable);
 
    // Buscar cliente activo por ID (evita retornar eliminados lógicamente)
    Optional<Client> findByIdClientAndIsActiveTrue(Integer idClient);
 
    // Verificar si ya existe un email registrado (para validar duplicados)
    boolean existsByEmail(String email);
 
    // Verificar si existe el email en otro cliente distinto (usado al actualizar)
    boolean existsByEmailAndIdClientNot(String email, Integer idClient);
}
