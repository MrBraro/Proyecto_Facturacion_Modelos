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

    // ── RF-13: Agregar producto a venta ───────────────────────────────────────

    @Transactional
    public SaleItemResponse addDetail(CreateSaleDetailRequest request) {
        var sale = saleRepository.findById(request.getSaleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venta no encontrada con id: " + request.getSaleId()));

        if (sale.getState() != SaleStatus.ABIERTA) {
            throw new BusinessException(
                    "La venta con id " + request.getSaleId() + " no está abierta. Estado actual: " + sale.getState());
        }

        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con id: " + request.getProductId()));

        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException(
                    "Stock insuficiente para el producto " + product.getName() +
                            ". Disponible: " + product.getStock() + ", solicitado: " + request.getQuantity());
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

        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        return saleDetailMapper.toResponse(saleDetailRepository.save(detail));
    }

    // ── Actualizar cantidad de producto ────────────────────────────────

    @Transactional
    public SaleItemResponse updateDetail(Integer detailId, UpdateSaleDetailRequest request) {
        SaleDetail detail = getOrThrow(detailId);

        if (detail.getSale().getState() != SaleStatus.ABIERTA) {
            throw new BusinessException(
                    "No se puede modificar un detalle de una venta que no está abierta");
        }

        var product = detail.getProduct();
        int diferencia = request.getQuantity() - detail.getQuantity();

        if (diferencia > 0 && product.getStock() < diferencia) {
            throw new BusinessException(
                    "Stock insuficiente para el producto " + product.getName() +
                            ". Disponible: " + product.getStock() + ", adicional solicitado: " + diferencia);
        }

        product.setStock(product.getStock() - diferencia);
        productRepository.save(product);

        detail.setQuantity(request.getQuantity());
        detail.setLineSubtotal(detail.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity())));

        return saleDetailMapper.toResponse(saleDetailRepository.save(detail));
    }

    // ── Eliminar producto de venta ─────────────────────────────────────

    @Transactional
    public void deleteDetail(Integer id) {

        SaleDetail detail = getOrThrow(id);

        if (detail.getSale().getState() != SaleStatus.ABIERTA) {
            throw new BusinessException(
                    "No se puede eliminar un detalle de una venta que no está abierta");
        }

        var product = detail.getProduct();
        product.setStock(product.getStock() + detail.getQuantity());
        productRepository.save(product);

        saleDetailRepository.delete(detail);
    }

    // ── Interno ───────────────────────────────────────────────────────────────

    private SaleDetail getOrThrow(Integer id) {
        return saleDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Detalle de venta no encontrado con id: " + id));
    }
}