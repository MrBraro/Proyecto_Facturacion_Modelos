package com.modelosgr86e1eq6.proyectofacturacion.users.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.users.entities.Role;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.users.dto.CreateUserRequest;
import com.modelosgr86e1eq6.proyectofacturacion.users.dto.UpdateUserRequest;
import com.modelosgr86e1eq6.proyectofacturacion.users.dto.UserSummaryResponse;
import com.modelosgr86e1eq6.proyectofacturacion.users.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")   // Toda la gestión de usuarios es solo ADMIN
@RequiredArgsConstructor
public class UserController {
 
    private final UserService userService;
 
    // ── GET /api/v1/users ──────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserSummaryResponse>>> getAll(
            @RequestParam(required = false) Role    role,
            @RequestParam(required = false) Integer branchId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
 
        Page<UserSummaryResponse> page = userService.findAll(role, branchId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }
 
    // ── GET /api/v1/users/{id} ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getById(
            @PathVariable Integer id) {
 
        return ResponseEntity.ok(ApiResponse.ok(userService.findById(id)));
    }
 
    // ── POST /api/v1/users — RF-SEG-10 ────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<UserSummaryResponse>> create(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal UserEntity admin,
            HttpServletRequest httpRequest) {
 
        UserSummaryResponse created = userService.createEmployee(
                request, admin, httpRequest.getRemoteAddr());
        return ResponseEntity.status(201).body(ApiResponse.ok("Empleado creado", created));
    }
 
    // ── PUT /api/v1/users/{id} ─────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserEntity admin,
            HttpServletRequest httpRequest) {
 
        UserSummaryResponse updated = userService.update(
                id, request, admin, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado", updated));
    }
 
    // ── PATCH /api/v1/users/{id}/deactivate ───────────────────────────────────
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserEntity admin,
            HttpServletRequest httpRequest) {
 
        userService.deactivate(id, admin, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.ok("Usuario desactivado"));
    }
 
    // ── PATCH /api/v1/users/{id}/activate ─────────────────────────────────────
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activate(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserEntity admin,
            HttpServletRequest httpRequest) {
 
        userService.activate(id, admin, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.ok("Usuario activado"));
    }
}