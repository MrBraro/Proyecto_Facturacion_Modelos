package com.modelosgr86e1eq6.proyectofacturacion.products.repositories;

import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Product}.
 *
 * <p>Todos los métodos de consulta filtran por {@code activo = true} para
 * garantizar que el soft delete sea consistente: ningún producto eliminado
 * (RF-05) puede ser leído, actualizado ni validado como si existiera.</p>
 *
 * <p>Convención de nombres Spring Data seguida en el proyecto:
 * el sufijo {@code AndActivoTrue} reemplaza el {@code findById} genérico
 * en cualquier operación sobre productos específicos.</p>
 *
 * @author MrBraro
 */
public interface ProductoRepository extends JpaRepository<Product, Integer> {

    /**
     * RF-02: Lista todos los productos activos del catálogo.
     *
     * @return lista de productos con {@code activo = true}
     */
    List<Product> findAllByActivoTrue();

    /**
     * RF-03: Busca un producto activo por su código semántico.
     *
     * @param codigo código del producto (ej. "P001")
     * @return {@link Optional} con el producto si existe y está activo
     */
    Optional<Product> findByCodigoAndActivoTrue(String codigo);

    /**
     * RF-04 / RF-05 / RF-06: Busca un producto activo por su PK interna.
     * Impide operar sobre productos eliminados con soft delete.
     *
     * @param id PK del producto
     * @return {@link Optional} con el producto si existe y está activo
     */
    Optional<Product> findByIdProductoAndActivoTrue(Integer id);

    /**
     * RF-01 / RF-04: Verifica si ya existe un producto activo con el código dado.
     * Al usar {@code AndActivoTrue}, un código liberado por soft delete
     * puede ser reutilizado en un nuevo producto.
     *
     * @param codigo código a verificar
     * @return {@code true} si existe un producto activo con ese código
     */
    boolean existsByCodigoAndActivoTrue(String codigo);
}
