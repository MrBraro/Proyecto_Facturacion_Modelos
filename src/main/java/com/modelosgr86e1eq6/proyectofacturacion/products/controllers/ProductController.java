package com.modelosgr86e1eq6.proyectofacturacion.products.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.CreateProductRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ProductResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.UpdateProductRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de productos.
 *
 * <p>Expone los endpoints del CRUD de productos (RF-01 a RF-05). Todos los
 * endpoints requieren autenticación mediante JWT (política global de
 * {@code SecurityConfig}).</p>
 *
 * <table border="1">
 *   <caption>Endpoints disponibles</caption>
 *   <tr><th>Método</th><th>Path</th><th>RF</th></tr>
 *   <tr><td>POST</td><td>/api/v1/products</td><td>RF-01</td></tr>
 *   <tr><td>GET</td><td>/api/v1/products</td><td>RF-02</td></tr>
 *   <tr><td>GET</td><td>/api/v1/products/{code}</td><td>RF-03</td></tr>
 *   <tr><td>PUT</td><td>/api/v1/products/{id}</td><td>RF-04</td></tr>
 *   <tr><td>DELETE</td><td>/api/v1/products/{id}</td><td>RF-05</td></tr>
 * </table>
 *
 * @author MrBraro
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ── RF-01: POST /api/v1/products ──────────────────────────────────────────

    /**
     * Registra un nuevo producto en el catálogo.
     *
     * @param request datos del producto a registrar
     * @return 201 Created con el producto creado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse created = productService.create(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.ok("Producto registrado", created));
    }

    // ── RF-02: GET /api/v1/products ───────────────────────────────────────────

    /**
     * Retorna la lista de todos los productos activos del catálogo.
     *
     * @return 200 OK con la lista de productos
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(productService.findAll()));
    }

    // ── RF-03: GET /api/v1/products/{code} ────────────────────────────────────

    /**
     * Consulta un producto activo por su código semántico.
     *
     * <p>{@code code} es el identificador externo del producto (ej. "P001"),
     * a diferencia de {@code id} que es la PK interna usada en escrituras.</p>
     *
     * @param code código del producto
     * @return 200 OK con el producto, o 404 si no existe o fue eliminado
     */
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<ProductResponse>> findByCode(
            @PathVariable String code) {

        return ResponseEntity.ok(ApiResponse.ok(productService.findByCode(code)));
    }

    // ── RF-04: PUT /api/v1/products/{id} ──────────────────────────────────────

    /**
     * Actualiza un producto activo existente.
     *
     * @param id      PK del producto a actualizar
     * @param request nuevos datos del producto
     * @return 200 OK con el producto actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse updated = productService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", updated));
    }

    // ── RF-05: DELETE /api/v1/products/{id} ───────────────────────────────────

    /**
     * Elimina lógicamente (soft delete) un producto activo.
     *
     * <p>El registro permanece en base de datos con {@code isActive = false}.
     * El producto no volverá a aparecer en listados ni búsquedas.</p>
     *
     * @param id PK del producto a eliminar
     * @return 200 OK con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado"));
    }
}
