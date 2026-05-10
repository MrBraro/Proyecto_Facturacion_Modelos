package com.modelosgr86e1eq6.proyectofacturacion.products.mappers;

import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ProductoResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir la entidad {@link Product} al DTO de salida {@link ProductoResponse}.
 *
 * <p>Sigue el mismo patrón que {@code AuditMapper}: componente Spring sin
 * dependencias externas, facilmente testeable de forma unitaria.</p>
 *
 * @author MrBraro
 */
@Component
public class ProductoMapper {

    /**
     * Convierte un {@link Product} a su representación pública {@link ProductoResponse}.
     *
     * @param product entidad a convertir; no debe ser {@code null}
     * @return DTO con los datos del producto
     */
    public ProductoResponse toResponse(Product product) {
        ProductoResponse response = new ProductoResponse();
        response.setId(product.getIdProducto());
        response.setCodigo(product.getCodigo());
        response.setNombre(product.getNombre());
        response.setPrecio(product.getPrecio());
        response.setDescripcion(product.getDescripcion());
        response.setStock(product.getStock());
        response.setActivo(product.isActivo());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}
