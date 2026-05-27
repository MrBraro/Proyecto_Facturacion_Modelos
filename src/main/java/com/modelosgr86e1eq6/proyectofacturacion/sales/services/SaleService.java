package com.modelosgr86e1eq6.proyectofacturacion.sales.services;

import com.modelosgr86e1eq6.proyectofacturacion.clients.repositories.ClientRepository;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductRepository;
import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.Handlers.SaleValidationHandler;
import com.modelosgr86e1eq6.proyectofacturacion.sales.CoR.context.SaleValidationContext;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.CreateSaleRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleDetailResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleSummaryResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleDetail;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.SaleStatus;
import com.modelosgr86e1eq6.proyectofacturacion.sales.mappers.SaleMapper;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleDetailRepository;
import com.modelosgr86e1eq6.proyectofacturacion.sales.repositories.SaleRepository;
import com.modelosgr86e1eq6.proyectofacturacion.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository       saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ClientRepository     clientRepository;
    private final UserRepository       userRepository;
    private final ProductRepository    productRepository;
    private final SaleMapper           saleMapper;
    private final SaleValidationHandler saleValidationChain;

    // ── RF-12: Create sale ────────────────────────────────────────────────────

    @Transactional
    public SaleDetailResponse create(CreateSaleRequest request) {
        var client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found with id: " + request.getClientId()));

        if (!client.isActive()) {
            throw new BusinessException("Client is not active");
        }

        // Obtener usuario autenticado del contexto de seguridad
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found: " + email));

        Sale sale = Sale.builder()
                .client(client)
                .user(user)
                .build();

        Sale saved = saleRepository.save(sale);
        return saleMapper.toDetail(saved, List.of());
    }

    // ── RF-14: Confirm sale ───────────────────────────────────────────────────

    @Transactional
    public SaleDetailResponse confirmSale(Integer id) {

        Sale sale = getOrThrow(id);

        var details = saleDetailRepository.findBySaleId(id);

        var context = new SaleValidationContext(sale, details);

        saleValidationChain.validate(context);

        details.forEach(detail -> {
            var product = detail.getProduct();
            product.setStock(
                    product.getStock()
                            - detail.getQuantity()
            );
        });

        // Calcular totales
        BigDecimal subtotal = details.stream()
                .map(SaleDetail::getLineSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal iva = subtotal
                .multiply(new BigDecimal("0.19"))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(iva)
                .setScale(2, RoundingMode.HALF_UP);

        sale.setSubtotal(subtotal);
        sale.setIva(iva);
        sale.setTotal(total);
        sale.setState(SaleStatus.CERRADA);

        Sale saved = saleRepository.save(sale);

        return saleMapper.toDetail(saved, details);
    }

    // ── RF-16: List sales ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<SaleSummaryResponse> findAll(Integer clientId, SaleStatus status,
                                             LocalDateTime from, LocalDateTime to,
                                             Pageable pageable) {
        return saleRepository
                .findByFilters(clientId, status, from, to, pageable)
                .map(saleMapper::toSummary);
    }

    // ── RF-17: Sale detail ────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public SaleDetailResponse findById(Integer id) {
        Sale sale = getOrThrow(id);
        return saleMapper.toDetail(sale, saleDetailRepository.findBySaleId(id));
    }

    // ── RF-18: Cancel sale ────────────────────────────────────────────────────

    @Transactional
    public SaleDetailResponse cancelSale(Integer id) {
        Sale sale = getOrThrow(id);

        if (sale.getState() == SaleStatus.CERRADA) {
            throw new BusinessException(
                    "Sale with id " + id + " is already closed");
        }

        if (sale.getState() == SaleStatus.ANULADA) {
            throw new BusinessException(
                    "Sale with id " + id + " is already cancelled");
        }

        var details = saleDetailRepository.findBySaleId(id);

        // Solo reintegrar stock si la venta fue confirmada antes de anularse
        if (sale.getState() == SaleStatus.CERRADA) {
            details.forEach(detail -> {
                var product = detail.getProduct();
                product.setStock(product.getStock() + detail.getQuantity());
                productRepository.save(product);
            });
        }

        sale.setState(SaleStatus.ANULADA);
        Sale saved = saleRepository.save(sale);
        return saleMapper.toDetail(saved, details);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private Sale getOrThrow(Integer id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sale not found with id: " + id));
    }
}