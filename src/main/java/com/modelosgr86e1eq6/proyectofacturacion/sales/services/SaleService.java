package com.modelosgr86e1eq6.proyectofacturacion.sales.services;

import com.modelosgr86e1eq6.proyectofacturacion.clients.repositories.ClientRepository;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductRepository;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.CreateSaleRequest;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleDetailResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.dto.SaleSummaryResponse;
import com.modelosgr86e1eq6.proyectofacturacion.sales.entities.Sale;
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

import java.time.LocalDateTime;
import java.util.List;

//El service contiene la lógica real de negocio. El comando solo lo referencia y lo llama:

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository       saleRepository;
    private final SaleDetailRepository saleDetailRepository;
    private final ClientRepository     clientRepository;
    private final UserRepository       userRepository;
    private final ProductRepository    productRepository;
    private final SaleMapper           saleMapper;

    // ── RF-12: Crear venta ────────────────────────────────────────────────────

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

    // ── RF-14: Confirmar venta ────────────────────────────────────────────────

    @Transactional
    public SaleDetailResponse confirmSale(Integer id) {
        Sale sale = getOrThrow(id);

        if (sale.getState() != SaleStatus.ABIERTA) {
            throw new BusinessException(
                    "Sale with id " + id + " cannot be confirmed. Current status: " + sale.getState());
        }

        var details = saleDetailRepository.findBySaleId(id);

        if (details.isEmpty()) {
            throw new BusinessException("Cannot confirm a sale with no products");
        }

        // Descontar stock al confirmar
        details.forEach(detail -> {
            var product = detail.getProduct();
            if (product.getStock() < detail.getQuantity()) {
                throw new BusinessException(
                        "Insufficient stock for product " + product.getName() +
                                ". Available: " + product.getStock() +
                                ", required: " + detail.getQuantity());
            }
            product.setStock(product.getStock() - detail.getQuantity());
            productRepository.save(product);
        });

        sale.setState(SaleStatus.CERRADA);
        Sale saved = saleRepository.save(sale);
        return saleMapper.toDetail(saved, details);
    }

    // ── RF-16: Consultar ventas ───────────────────────────────────────────────
    @Transactional(readOnly = true) public Page<SaleSummaryResponse> findAll(Integer clientId, SaleStatus estado, LocalDateTime from, LocalDateTime to, Pageable pageable) { return saleRepository .findByFilters(clientId, estado, from, to, pageable) .map(saleMapper::toSummary); }

    // ── RF-17: Consultar detalle de venta ─────────────────────────────────────

    @Transactional(readOnly = true)
    public SaleDetailResponse findById(Integer id) {
        Sale sale = getOrThrow(id);
        return saleMapper.toDetail(sale, saleDetailRepository.findBySaleId(id));
    }

    // ── Anular venta ───────────────────────────────────────────────────

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

    // ── Interno ───────────────────────────────────────────────────────────────

    private Sale getOrThrow(Integer id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Venta no encontrada con id: " + id));
    }
}