package com.modelosgr86e1eq6.proyectofacturacion.products.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.CreateProductRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ProductResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.UpdateProductRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de productos.
 *
 * <p>Implementa los endpoints del CRUD de productos (RF-01 a RF-06) 
 * con protección por roles basada en JWT.</p>
 *
 * @author MrBraro
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ── RF-01: POST /api/v1/products ──────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse created = productService.create(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.ok("Producto registrado", created));
    }

    // ── RF-02: GET /api/v1/products ───────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code) {
            
        List<ProductResponse> products = productService.findAll(name, code);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    // ── RF-06: GET /api/v1/products/alerts ────────────────────────────────────
    
    // NOTA: Este endpoint va ANTES de /{id} para evitar que Spring lo confunda
    // con un ID = "alerts".
    @GetMapping("/alerts")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAlerts() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getAlerts()));
    }

    // ── RF-03: GET /api/v1/products/{id} ──────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(ApiResponse.ok(productService.findById(id)));
    }

    // ── RF-04: PATCH /api/v1/products/{id} ────────────────────────────────────

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse updated = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", updated));
    }

    // ── RF-05: DELETE /api/v1/products/{id} ───────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto desactivado"));
    }
}
