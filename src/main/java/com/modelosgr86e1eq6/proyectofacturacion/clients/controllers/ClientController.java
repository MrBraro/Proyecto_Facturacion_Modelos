package com.modelosgr86e1eq6.proyectofacturacion.clients.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.clients.dto.ClientResponse;
import com.modelosgr86e1eq6.proyectofacturacion.clients.dto.CreateClientRequest;
import com.modelosgr86e1eq6.proyectofacturacion.clients.dto.UpdateClientRequest;
import com.modelosgr86e1eq6.proyectofacturacion.clients.services.ClientService;
import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {
 
    private final ClientService clientService;
 
    // ── RF-07: Registrar cliente ──────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<ClientResponse>> create(
            @Valid @RequestBody CreateClientRequest request) {
 
        ClientResponse response = clientService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cliente registrado exitosamente", response));
    }
 
    // ── RF-08: Listar clientes ────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Page<ClientResponse>>> findAll(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
 
        Page<ClientResponse> page = clientService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }
 
    // ── RF-09: Consultar cliente por ID ───────────────────────────────────
    @GetMapping("/{idClient}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<ClientResponse>> findById(
            @PathVariable Integer idClient) {
 
        ClientResponse response = clientService.findById(idClient);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
 
    // ── RF-10: Actualizar cliente ─────────────────────────────────────────
    @PatchMapping("/{idClient}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<ClientResponse>> update(
            @PathVariable Integer idClient,
            @Valid @RequestBody UpdateClientRequest request) {
 
        ClientResponse response = clientService.update(idClient, request);
        return ResponseEntity.ok(ApiResponse.ok("Cliente actualizado exitosamente", response));
    }
 
    // ── RF-11: Eliminar cliente (soft delete) ─────────────────────────────
    @DeleteMapping("/{idClient}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer idClient) {
 
        clientService.delete(idClient);
        return ResponseEntity.ok(ApiResponse.ok("Cliente eliminado exitosamente"));
    }
}
