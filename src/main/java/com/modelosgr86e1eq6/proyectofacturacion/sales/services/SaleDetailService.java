package com.modelosgr86e1eq6.proyectofacturacion.sales.services;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductRepository;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.CreateSaleDetailRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleItemResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.UpdateSaleDetailRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleStatus;
import com.modelosgr86e1eq6.proyectofacturacion.sales.mappers.SaleDetailMapper;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleDetailRepository;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SaleDetailService {

    private final SaleDetailRepository saleDetailRepository;
    private final SaleRepository       saleRepository;
    private final ProductRepository    productRepository;
    private final SaleDetailMapper     saleDetailMapper;

    // ── RF-13: Add product to sale ────────────────────────────────────────────

    @Transactional
    public SaleItemResponse addDetail(Integer saleId, CreateSaleDetailRequest request) {
        var sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sale not found with id: " + saleId));

        if (sale.getState() != SaleStatus.ABIERTA) {
            throw new BusinessException(
                    "Sale with id " + saleId +
                            " is not open. Current status: " + sale.getState());
        }

        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        // Solo validamos que haya stock suficiente, no lo descontamos aún
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException(
                    "Insufficient stock for product " + product.getName() +
                            ". Available: " + product.getStock() +
                            ", requested: " + request.getQuantity());
        }

        BigDecimal lineSubtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        SaleDetail detail = SaleDetail.builder()
                .sale(sale)
                .product(product)
                .quantity(request.getQuantity())
                .unitPrice(product.getPrice())
                .lineSubtotal(lineSubtotal)
                .build();

        return saleDetailMapper.toResponse(saleDetailRepository.save(detail));
    }

    // ── RF-19: Update product quantity ────────────────────────────────────────

    @Transactional
    public SaleItemResponse updateDetail(Integer detailId, UpdateSaleDetailRequest request) {
        SaleDetail detail = getOrThrow(detailId);

        if (detail.getSale().getState() != SaleStatus.ABIERTA) {
            throw new BusinessException(
                    "Cannot modify a detail from a sale that is not open");
        }

        var product = detail.getProduct();

        // Solo validamos stock para la cantidad nueva, no tocamos el stock
        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException(
                    "Insufficient stock for product " + product.getName() +
                            ". Available: " + product.getStock() +
                            ", requested: " + request.getQuantity());
        }

        detail.setQuantity(request.getQuantity());
        detail.setLineSubtotal(detail.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity())));

        return saleDetailMapper.toResponse(saleDetailRepository.save(detail));
    }

    // ── RF-20: Delete product from sale ──────────────────────────────────────

    @Transactional
    public void deleteDetail(Integer id) {
        SaleDetail detail = getOrThrow(id);

        if (detail.getSale().getState() != SaleStatus.ABIERTA) {
            throw new BusinessException(
                    "Cannot delete a detail from a sale that is not open");
        }

        // Solo eliminamos el detalle, el stock no se tocó aún
        saleDetailRepository.delete(detail);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private SaleDetail getOrThrow(Integer id) {
        return saleDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sale detail not found with id: " + id));
    }
}