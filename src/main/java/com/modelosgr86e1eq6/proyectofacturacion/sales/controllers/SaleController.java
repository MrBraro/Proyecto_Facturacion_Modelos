package com.modelosgr86e1eq6.proyectofacturacion.sales.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.CreateSaleDetailRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleItemResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.UpdateSaleDetailRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.services.SaleDetailService;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleDetailResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.CreateSaleRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleSummaryResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleStatus;
import com.modelosgr86e1eq6.proyectofacturacion.sales.services.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService       saleService;
    private final SaleDetailService saleDetailService;

    // ── RF-12: Crear venta POST http://localhost:8082/api/v1/sales ────────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> create(
            @Valid @RequestBody CreateSaleRequest request) {

        SaleDetailResponse response = saleService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Venta registrada exitosamente", response));
    }

    // ── RF-13: Agregar productos a una venta POST http://localhost:8082/api/v1/sales/{id}/products ──────────────────────────────────
    @PostMapping("/{id}/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleItemResponse>> addProduct(
            @PathVariable Integer id,
            @Valid @RequestBody CreateSaleDetailRequest request) {

        request.setSaleId(id);
        SaleItemResponse response = saleDetailService.addDetail(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Producto agregado exitosamente", response));
    }

    // ── RF-14: Calcular total de venta PUT http://localhost:8082/api/v1/sales/{id}/confirm ────────────────────────────────────────
    // ── RF-15: Actualizar inventario ──────────────────────────────────────────
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> confirm(
            @PathVariable Integer id) {

        SaleDetailResponse response = saleService.confirmSale(id);
        return ResponseEntity.ok(ApiResponse.ok("Venta confirmada exitosamente", response));
    }

    // ── RF-16: Consultar ventas GET http://localhost:8082/api/v1/sales?page=0&size=10 ───────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Page<SaleSummaryResponse>>> findAll(
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) SaleStatus estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 10, sort = "saleDate") Pageable pageable) {

        Page<SaleSummaryResponse> page = saleService.findAll(clientId, estado, from, to, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    // ── RF-17: Consultar detalle de venta GET http://localhost:8082/api/v1/sales/{id} ─────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> findById(
            @PathVariable Integer id) {

        SaleDetailResponse response = saleService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── Anular venta PATCH http://localhost:8082/api/v1/sales/{id}/cancel ───────────────────────────────────────────────────
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> cancel(
            @PathVariable Integer id) {

        SaleDetailResponse response = saleService.cancelSale(id);
        return ResponseEntity.ok(ApiResponse.ok("Venta anulada exitosamente", response));
    }

    // ── Actualizar cantidad de producto PATCH http://localhost:8082/api/v1/sales/{id}/products/{detailId} ────────────────────────────────
    @PatchMapping("/{id}/products/{detailId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleItemResponse>> updateProduct(
            @PathVariable Integer id,
            @PathVariable Integer detailId,
            @Valid @RequestBody UpdateSaleDetailRequest request) {

        SaleItemResponse response = saleDetailService.updateDetail(detailId, request);
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado exitosamente", response));
    }

    // ── Eliminar producto de venta DELETE http://localhost:8082/api/v1/sales/{saleId}/details/{detailId} ─────────────────────────────────────
    @DeleteMapping("/{saleId}/details/{detailId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Integer saleId,
            @PathVariable Integer detailId) {

        saleDetailService.deleteDetail(detailId);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado exitosamente"));
    }
}