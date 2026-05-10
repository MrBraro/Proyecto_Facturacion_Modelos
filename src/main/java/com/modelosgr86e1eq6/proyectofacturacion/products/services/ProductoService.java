package com.modelosgr86e1eq6.proyectofacturacion.products.services;

import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ActualizarProductoRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.CrearProductoRequest;
import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ProductoResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import com.modelosgr86e1eq6.proyectofacturacion.products.exceptions.StockInsuficienteException;
import com.modelosgr86e1eq6.proyectofacturacion.products.mappers.ProductoMapper;
import com.modelosgr86e1eq6.proyectofacturacion.products.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de negocio para la gestión del catálogo de productos.
 *
 * <p>Implementa los requerimientos RF-01 a RF-06:</p>
 * <ul>
 *   <li>RF-01: {@link #crear}</li>
 *   <li>RF-02: {@link #listar}</li>
 *   <li>RF-03: {@link #buscarPorCodigo}</li>
 *   <li>RF-04: {@link #actualizar}</li>
 *   <li>RF-05: {@link #eliminar} (soft delete)</li>
 *   <li>RF-06: {@link #validarStock}</li>
 * </ul>
 *
 * @author MrBraro
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper     productoMapper;

    // ── RF-01: Registrar producto ─────────────────────────────────────────────

    /**
     * Registra un nuevo producto en el catálogo.
     *
     * <p>Valida que el código no esté en uso por otro producto activo antes de persistir.
     * Un código liberado por soft delete puede reutilizarse.</p>
     *
     * @param request datos del nuevo producto
     * @return DTO con el producto creado
     * @throws BusinessException si el código ya pertenece a un producto activo
     */
    @Transactional
    public ProductoResponse crear(CrearProductoRequest request) {
        if (productoRepository.existsByCodigoAndActivoTrue(request.getCodigo())) {
            throw new BusinessException(
                    "Ya existe un producto activo con el código: " + request.getCodigo());
        }

        Product producto = Product.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .descripcion(request.getDescripcion())
                .stock(request.getStock())
                .build();

        productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    // ── RF-02: Consultar productos ────────────────────────────────────────────

    /**
     * Retorna la lista de todos los productos activos del catálogo.
     *
     * <p>Los productos eliminados (soft delete) no aparecen en el listado.</p>
     *
     * @return lista de productos activos; vacía si no hay ninguno
     */
    public List<ProductoResponse> listar() {
        return productoRepository.findAllByActivoTrue()
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    // ── RF-03: Consultar producto específico ──────────────────────────────────

    /**
     * Busca un producto activo por su código semántico.
     *
     * @param codigo código del producto a consultar (ej. "P001")
     * @return DTO del producto
     * @throws ResourceNotFoundException si el código no existe o el producto fue eliminado
     */
    public ProductoResponse buscarPorCodigo(String codigo) {
        Product producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con código: " + codigo));
        return productoMapper.toResponse(producto);
    }

    // ── RF-04: Actualizar producto ────────────────────────────────────────────

    /**
     * Actualiza los datos de un producto activo existente.
     *
     * <p>Si el código cambia, verifica que el nuevo código no esté en uso por
     * otro producto activo. No es posible actualizar un producto eliminado.</p>
     *
     * @param id      PK del producto a actualizar
     * @param request nuevos datos del producto
     * @return DTO con el producto actualizado
     * @throws ResourceNotFoundException si el producto no existe o fue eliminado
     * @throws BusinessException         si el nuevo código ya está en uso
     */
    @Transactional
    public ProductoResponse actualizar(Integer id, ActualizarProductoRequest request) {
        Product producto = getActivoOrThrow(id);

        boolean cambiaCodigo = !producto.getCodigo().equals(request.getCodigo());
        if (cambiaCodigo && productoRepository.existsByCodigoAndActivoTrue(request.getCodigo())) {
            throw new BusinessException(
                    "Ya existe un producto activo con el código: " + request.getCodigo());
        }

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setDescripcion(request.getDescripcion());
        producto.setStock(request.getStock());

        productoRepository.save(producto);
        return productoMapper.toResponse(producto);
    }

    // ── RF-05: Eliminar producto (soft delete) ────────────────────────────────

    /**
     * Realiza el soft delete de un producto: marca {@code activo = false}.
     *
     * <p>El registro permanece en base de datos para trazabilidad. El código
     * del producto queda disponible para ser reutilizado en un registro nuevo.</p>
     *
     * @param id PK del producto a eliminar
     * @throws ResourceNotFoundException si el producto no existe o ya fue eliminado
     */
    @Transactional
    public void eliminar(Integer id) {
        Product producto = getActivoOrThrow(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    // ── RF-06: Validar stock ──────────────────────────────────────────────────

    /**
     * Valida que un producto activo tenga stock suficiente para la cantidad requerida.
     *
     * <p>Este método está diseñado para ser reutilizado desde {@code VentaService}
     * antes de registrar una venta.</p>
     *
     * <p><strong>⚠ TODO (deuda técnica):</strong> este método no es atómico.
     * Bajo concurrencia alta (dos ventas simultáneas del mismo producto), dos hilos
     * pueden pasar la guarda antes de que alguno descuente el stock, resultando en
     * ventas sobre stock cero. Resolver en {@code VentaService} con:</p>
     * <ul>
     *   <li>{@code @Lock(LockModeType.PESSIMISTIC_WRITE)} en {@code ProductoRepository}, o</li>
     *   <li>{@code @Version} (optimistic locking) en la entidad {@link Product}.</li>
     * </ul>
     *
     * @param productoId        PK del producto a validar
     * @param cantidadRequerida cantidad que se desea descontar; debe ser mayor a cero
     * @throws BusinessException         si {@code cantidadRequerida} es ≤ 0
     * @throws ResourceNotFoundException si el producto no existe o fue eliminado
     * @throws StockInsuficienteException si el stock disponible es menor a la cantidad requerida
     */
    public void validarStock(Integer productoId, int cantidadRequerida) {
        if (cantidadRequerida <= 0) {
            throw new BusinessException("La cantidad requerida debe ser mayor a cero");
        }

        Product producto = getActivoOrThrow(productoId);

        if (producto.getStock() < cantidadRequerida) {
            throw new StockInsuficienteException(
                    productoId, producto.getStock(), cantidadRequerida);
        }
    }

    // ── Interno ───────────────────────────────────────────────────────────────

    /**
     * Obtiene un producto activo por su PK o lanza {@link ResourceNotFoundException}.
     *
     * <p>Centraliza la guarda de soft delete para todos los métodos de escritura,
     * evitando modificar o validar productos eliminados.</p>
     *
     * @param id PK del producto
     * @return entidad {@link Product} activa
     * @throws ResourceNotFoundException si el producto no existe o está inactivo
     */
    private Product getActivoOrThrow(Integer id) {
        return productoRepository.findByIdProductoAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con id: " + id));
    }
}
