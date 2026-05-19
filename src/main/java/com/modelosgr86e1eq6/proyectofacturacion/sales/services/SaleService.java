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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    // ── RF-12: Crear venta ────────────────────────────────────────────────────

    @Transactional
    public SaleDetailResponse create(CreateSaleRequest request) {
        var client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + request.getClientId()));

        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + request.getUserId()));

        if (!client.isActive()) {
            throw new BusinessException("El cliente no se encuentra activo");
        }

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

        // ── Validar estado ───────────────────────────────────────
        if (sale.getState() != SaleStatus.ABIERTA) {
            throw new BusinessException(
                    "La venta con id " + id +
                            " no puede confirmarse. Estado actual: " +
                            sale.getState());
        }

        // ── Obtener detalles ────────────────────────────────────
        var details = saleDetailRepository.findBySaleId(id);

        if (details.isEmpty()) {
            throw new BusinessException(
                    "No se puede confirmar una venta sin productos");
        }

        // ── Validar stock ───────────────────────────────────────
        details.forEach(detail -> {

            var product = detail.getProduct();

            if (product.getStock() < detail.getQuantity()) {
                throw new BusinessException(
                        "Stock insuficiente para el producto: "
                                + product.getName());
            }
        });

        // ── Calcular subtotal ───────────────────────────────────
        double subtotal = details.stream()
                .mapToDouble(detail ->
                        detail.getQuantity() *
                                detail.getUnitPrice().doubleValue())
                .sum();

        // ── Calcular IVA y total ────────────────────────────────
        double iva = subtotal * 0.19;
        double total = subtotal + iva;

        // ── Asignar valores a la venta ──────────────────────────
        sale.setSubtotal(BigDecimal.valueOf(subtotal));
        sale.setIva(BigDecimal.valueOf(iva));
        sale.setTotal(BigDecimal.valueOf(total));

        // ── Actualizar inventario ───────────────────────────────
        details.forEach(detail -> {

            var product = detail.getProduct();

            product.setStock(
                    product.getStock() - detail.getQuantity());

            productRepository.save(product);
        });

        // ── Confirmar venta ─────────────────────────────────────
        sale.setState(SaleStatus.CERRADA);

        Sale saved = saleRepository.save(sale);

        return saleMapper.toDetail(saved, details);
    }

    // ── RF-16: Consultar ventas ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<SaleSummaryResponse> findAll(
            Integer clientId,
            SaleStatus estado,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        return saleRepository
                .findByFilters(clientId, estado, from, to, pageable)
                .map(saleMapper::toSummary);
    }

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
                    "La venta con id " + id + " ya está cerrada y no puede anularse");
        }

        if (sale.getState() == SaleStatus.ANULADA) {
            throw new BusinessException("La venta con id " + id + " ya está anulada");
        }

        var details = saleDetailRepository.findBySaleId(id);
        details.forEach(detail -> {
            var product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        });

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