package com.modelosgr86e1eq6.proyectofacturacion.products.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ActualizarProductoRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.CrearProductoRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ProductoResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.services.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión del catálogo de productos.
 *
 * <p>Expone los endpoints del CRUD de productos (RF-01 a RF-05). Todos los
 * endpoints requieren autenticación mediante JWT (política global definida en
 * {@code SecurityConfig}). El control de roles fino puede añadirse con
 * {@code @PreAuthorize} sin modificar esta clase.</p>
 *
 * <table border="1">
 *   <caption>Endpoints disponibles</caption>
 *   <tr><th>Método</th><th>Path</th><th>RF</th></tr>
 *   <tr><td>POST</td><td>/api/v1/productos</td><td>RF-01</td></tr>
 *   <tr><td>GET</td><td>/api/v1/productos</td><td>RF-02</td></tr>
 *   <tr><td>GET</td><td>/api/v1/productos/{codigo}</td><td>RF-03</td></tr>
 *   <tr><td>PUT</td><td>/api/v1/productos/{id}</td><td>RF-04</td></tr>
 *   <tr><td>DELETE</td><td>/api/v1/productos/{id}</td><td>RF-05</td></tr>
 * </table>
 *
 * @author MrBraro
 */
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // ── RF-01: POST /api/v1/productos ─────────────────────────────────────────

    /**
     * Registra un nuevo producto en el catálogo.
     *
     * @param request datos del producto a registrar
     * @return 201 Created con el producto creado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(
            @Valid @RequestBody CrearProductoRequest request) {

        ProductoResponse creado = productoService.crear(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.ok("Producto registrado", creado));
    }

    // ── RF-02: GET /api/v1/productos ──────────────────────────────────────────

    /**
     * Retorna la lista de todos los productos activos del catálogo.
     *
     * @return 200 OK con la lista de productos
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listar()));
    }

    // ── RF-03: GET /api/v1/productos/{codigo} ─────────────────────────────────

    /**
     * Consulta un producto activo por su código semántico.
     *
     * <p>{@code codigo} es el identificador externo/semántico del producto
     * (ej. "P001"), a diferencia de {@code id} que es la PK interna.
     * El enunciado RF-03 especifica este endpoint con {@code {codigo}}.</p>
     *
     * @param codigo código del producto (ej. "P001")
     * @return 200 OK con el producto, o 404 si no existe o fue eliminado
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<ApiResponse<ProductoResponse>> buscarPorCodigo(
            @PathVariable String codigo) {

        return ResponseEntity.ok(ApiResponse.ok(productoService.buscarPorCodigo(codigo)));
    }

    // ── RF-04: PUT /api/v1/productos/{id} ─────────────────────────────────────

    /**
     * Actualiza un producto activo existente.
     *
     * @param id      PK del producto a actualizar
     * @param request nuevos datos del producto
     * @return 200 OK con el producto actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarProductoRequest request) {

        ProductoResponse actualizado = productoService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", actualizado));
    }

    // ── RF-05: DELETE /api/v1/productos/{id} ──────────────────────────────────

    /**
     * Elimina lógicamente (soft delete) un producto activo.
     *
     * <p>El registro permanece en base de datos con {@code activo = false}.
     * El producto no volverá a aparecer en listados ni búsquedas.</p>
     *
     * @param id PK del producto a eliminar
     * @return 200 OK con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado"));
    }
}
