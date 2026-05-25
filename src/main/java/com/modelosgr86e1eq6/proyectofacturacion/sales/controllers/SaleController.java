package com.modelosgr86e1eq6.proyectofacturacion.sales.controllers;

import com.modelosgr86e1eq6.proyectofacturacion.common.dto.ApiResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.commands.*;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.*;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleStatus;
import com.modelosgr86e1eq6.proyectofacturacion.sales.services.SaleDetailService;
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

//El controller recibe la solicitud HTTP, crea el comando con los datos necesarios y lo ejecuta. No sabe cómo se procesa, solo dispara:

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService       saleService;
    private final SaleDetailService saleDetailService;

    // ── RF-12: Create sale ────────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> create(
            @Valid @RequestBody CreateSaleRequest request) {

        SaleDetailResponse response = new CreateSaleCommand(saleService, request).execute();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Sale created successfully", response));
    }

    // ── RF-13 + RF-15: Add product to sale ────────────────────────────────────
    @PostMapping("/{id}/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleItemResponse>> addProduct(
            @PathVariable Integer id,
            @Valid @RequestBody CreateSaleDetailRequest request) {

        SaleItemResponse response = new AddProductCommand(saleDetailService, id, request).execute();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product added successfully", response));
    }

    // ── RF-14: Confirm sale ───────────────────────────────────────────────────
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> confirm(
            @PathVariable Integer id) {

        SaleDetailResponse response = new ConfirmSaleCommand(saleService, id).execute();
        return ResponseEntity.ok(ApiResponse.ok("Sale confirmed successfully", response));
    }

    // ── RF-16: List sales ─────────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<Page<SaleSummaryResponse>>> findAll(
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) SaleStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 10, sort = "saleDate") Pageable pageable) {

        Page<SaleSummaryResponse> page = saleService.findAll(clientId, status, from, to, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    // ── RF-17: Sale detail ────────────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> findById(
            @PathVariable Integer id) {

        SaleDetailResponse response = saleService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── RF-18: Cancel sale ────────────────────────────────────────────────────
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> cancel(
            @PathVariable Integer id) {

        SaleDetailResponse response = new CancelSaleCommand(saleService, id).execute();
        return ResponseEntity.ok(ApiResponse.ok("Sale cancelled successfully", response));
    }

    // ── RF-19: Update product quantity ────────────────────────────────────────
    @PatchMapping("/{id}/products/{detailId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @SuppressWarnings("unused")
    public ResponseEntity<ApiResponse<SaleItemResponse>> updateProduct(
            @PathVariable Integer id,
            @PathVariable Integer detailId,
            @Valid @RequestBody UpdateSaleDetailRequest request) {

        SaleItemResponse response = new UpdateProductCommand(saleDetailService, detailId, request).execute();
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", response));
    }

    // ── RF-20: Delete product from sale ──────────────────────────────────────
    @DeleteMapping("/{id}/products/{detailId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @SuppressWarnings("unused")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Integer id,
            @PathVariable Integer detailId) {

        new RemoveProductCommand(saleDetailService, detailId).execute();
        return ResponseEntity.ok(ApiResponse.ok("Product removed successfully"));
    }
}