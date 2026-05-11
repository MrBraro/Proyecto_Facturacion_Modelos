package com.modelosgr86e1eq6.proyectofacturacion.products.repositories;

import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Product}.
 *
 * <p>Todos los métodos filtran por {@code isActive = true} para garantizar
 * que el soft delete sea consistente: ningún producto eliminado (RF-05)
 * puede ser leído, actualizado ni validado como si existiera.</p>
 *
 * @author MrBraro
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * RF-02: Lista todos los productos activos del catálogo con filtros opcionales.
     *
     * @param name     filtro por nombre (LIKE case-insensitive), puede ser nulo
     * @param code     filtro por código exacto, puede ser nulo
     * @return lista de productos activos que coincidan con los filtros
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true " +
           "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:code IS NULL OR p.code = :code)")
    List<Product> findByFilters(
            @Param("name") String name,
            @Param("code") String code);

    /**
     * RF-03: Busca un producto activo por su PK interna.
     * Impide operar sobre productos eliminados con soft delete.
     *
     * @param id PK del producto
     * @return {@link Optional} con el producto si existe y está activo
     */
    Optional<Product> findByIdProductAndIsActiveTrue(Integer id);

    /**
     * RF-01 / RF-04: Verifica si existe un producto activo con el código dado.
     * Al filtrar por {@code isActive}, un código liberado por soft delete
     * puede reutilizarse en un nuevo producto.
     *
     * @param code código a verificar
     * @return {@code true} si existe un producto activo con ese código
     */
    boolean existsByCodeAndIsActiveTrue(String code);

    /**
     * RF-06: Obtener alertas de productos.
     * Retorna productos activos cuyo stock actual está por debajo de un umbral.
     *
     * @param threshold umbral de stock (ej. 10)
     * @return lista de productos con stock crítico
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stock <= :threshold")
    List<Product> findProductsWithLowStock(@Param("threshold") int threshold);
}
