package com.modelosgr86e1eq6.proyectofacturacion.products.services;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.CreateProductRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ProductResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.UpdateProductRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import com.modelosgr86e1eq6.proyectofacturacion.products.exceptions.InsufficientStockException;
import com.modelosgr86e1eq6.proyectofacturacion.products.mappers.ProductMapper;
import com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión del catálogo de productos.
 *
 * <p>Implementa los requerimientos RF-01 a RF-06:</p>
 * <ul>
 *   <li>RF-01: {@link #create}</li>
 *   <li>RF-02: {@link #findAll}</li>
 *   <li>RF-03: {@link #findByCode}</li>
 *   <li>RF-04: {@link #update}</li>
 *   <li>RF-05: {@link #delete} (soft delete)</li>
 *   <li>RF-06: {@link #validateStock}</li>
 * </ul>
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
     *
     * @param request datos del nuevo producto
     * @return DTO con el producto creado
     * @throws BusinessException si ya existe un producto activo con el mismo código
     */
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new BusinessException(
                    "Ya existe un producto activo con el código: " + request.getCode());
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
     * Retorna la lista de todos los productos activos del catálogo.
     *
     * @return lista de productos activos; vacía si no hay ninguno
     */
    public List<ProductResponse> findAll() {
        return productRepository.findAllByIsActiveTrue()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    // ── RF-03: Find product by code ───────────────────────────────────────────

    /**
     * Busca un producto activo por su código semántico.
     *
     * @param code código del producto (ej. "P001")
     * @return DTO del producto
     * @throws ResourceNotFoundException si el código no existe o el producto fue eliminado
     */
    public ProductResponse findByCode(String code) {
        Product product = productRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con código: " + code));
        return productMapper.toResponse(product);
    }

    // ── RF-04: Update product ─────────────────────────────────────────────────

    /**
     * Actualiza los datos de un producto activo existente.
     *
     * <p>Si el código cambia, verifica que el nuevo código no esté en uso.
     * No es posible actualizar un producto eliminado (soft deleted).</p>
     *
     * @param id      PK del producto a actualizar
     * @param request nuevos datos del producto
     * @return DTO con el producto actualizado
     * @throws ResourceNotFoundException si el producto no existe o fue eliminado
     * @throws BusinessException         si el nuevo código ya está en uso
     */
    @Transactional
    public ProductResponse update(Integer id, UpdateProductRequest request) {
        Product product = getActiveOrThrow(id);

        boolean codeChanged = !product.getCode().equals(request.getCode());
        if (codeChanged && productRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new BusinessException(
                    "Ya existe un producto activo con el código: " + request.getCode());
        }

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
     * Realiza el soft delete de un producto: marca {@code isActive = false}.
     *
     * <p>El registro permanece en base de datos para trazabilidad histórica.
     * El código queda disponible para ser reutilizado en un nuevo producto.</p>
     *
     * @param id PK del producto a eliminar
     * @throws ResourceNotFoundException si el producto no existe o ya fue eliminado
     */
    @Transactional
    public void delete(Integer id) {
        Product product = getActiveOrThrow(id);
        product.setActive(false);
        productRepository.save(product);
    }

    // ── RF-06: Validate stock ─────────────────────────────────────────────────

    /**
     * Valida que un producto activo tenga stock suficiente para la cantidad requerida.
     *
     * <p>Diseñado para ser reutilizado desde {@code VentaService} antes de registrar
     * una venta.</p>
     *
     * <p><strong>⚠ TODO (deuda técnica):</strong> este método no es atómico.
     * Bajo concurrencia alta (dos ventas simultáneas del mismo producto), dos hilos
     * pueden pasar la guarda antes de que alguno descuente el stock. Resolver en
     * {@code VentaService} con:</p>
     * <ul>
     *   <li>{@code @Lock(LockModeType.PESSIMISTIC_WRITE)} en {@code ProductRepository}, o</li>
     *   <li>{@code @Version} (optimistic locking) en la entidad {@link Product}.</li>
     * </ul>
     *
     * @param productId        PK del producto a validar
     * @param requiredQuantity cantidad que se desea descontar; debe ser mayor a cero
     * @throws BusinessException          si {@code requiredQuantity} es ≤ 0
     * @throws ResourceNotFoundException  si el producto no existe o fue eliminado
     * @throws InsufficientStockException si el stock disponible es menor a la cantidad requerida
     */
    public void validateStock(Integer productId, int requiredQuantity) {
        if (requiredQuantity <= 0) {
            throw new BusinessException("La cantidad requerida debe ser mayor a cero");
        }

        Product product = getActiveOrThrow(productId);

        if (product.getStock() < requiredQuantity) {
            throw new InsufficientStockException(productId, product.getStock(), requiredQuantity);
        }
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    /**
     * Obtiene un producto activo por su PK o lanza {@link ResourceNotFoundException}.
     *
     * <p>Centraliza la guarda de soft delete para todos los métodos de escritura.</p>
     *
     * @param id PK del producto
     * @return entidad {@link Product} activa
     * @throws ResourceNotFoundException si el producto no existe o está inactivo
     */
    private Product getActiveOrThrow(Integer id) {
        return productRepository.findByIdProductAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con id: " + id));
    }
}
