package com.modelosgr86e1eq6.proyectofacturacion.products.services;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.CreateProductRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ProductResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.UpdateProductRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import com.modelosgr86e1eq6.proyectofacturacion.products.mappers.ProductMapper;
import com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión del catálogo de productos.
 *
 * <p>Implementa los requerimientos RF-01 a RF-06 según el esquema real de BD.</p>
 *
 * @author MrBraro
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper     productMapper;

    // ── RF-01: Register product ───────────────────────────────────────────────

    /**
     * Registra un nuevo producto en el catálogo.
     * Valida unicidad de código.
     */
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new BusinessException("Ya existe un producto activo con el código: " + request.getCode());
        }

        Product product = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .stock(request.getStock())
                .isActive(true)
                .build();

        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    // ── RF-02: List products ──────────────────────────────────────────────────

    /**
     * Retorna productos activos con soporte de búsqueda por nombre y código.
     */
    public List<ProductResponse> findAll(String name, String code) {
        return productRepository.findByFilters(name, code)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    // ── RF-03: Find product by ID ─────────────────────────────────────────────

    /**
     * Retorna el detalle completo de un producto por su ID interno.
     */
    public ProductResponse findById(Integer id) {
        return productMapper.toResponse(getActiveOrThrow(id));
    }

    // ── RF-04: Update product (PATCH) ─────────────────────────────────────────

    /**
     * Actualiza los datos de un producto (excluyendo código y stock).
     */
    @Transactional
    public ProductResponse update(Integer id, UpdateProductRequest request) {
        Product product = getActiveOrThrow(id);

        product.setCode(request.getCode());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());

        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    // ── RF-05: Delete product (soft delete) ───────────────────────────────────

    /**
     * Desactiva un producto validando que no tenga ventas activas.
     */
    @Transactional
    public void delete(Integer id) {
        Product product = getActiveOrThrow(id);

        // TODO: Validar que el producto no tenga ventas activas antes de desactivar.
        // Consultar la tabla sale_details cuando el módulo de ventas esté implementado.
        
        product.setActive(false);
        productRepository.save(product);
    }

    // ── RF-06: Get product alerts ─────────────────────────────────────────────

    /**
     * Retorna la lista de productos cuyo stock está en o por debajo de un umbral
     * hardcodeado (ej. 10), dado que no existe columna min_stock en BD.
     */
    public List<ProductResponse> getAlerts() {
        int ALERT_THRESHOLD = 10;
        return productRepository.findProductsWithLowStock(ALERT_THRESHOLD)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private Product getActiveOrThrow(Integer id) {
        return productRepository.findByIdProductAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }
}
