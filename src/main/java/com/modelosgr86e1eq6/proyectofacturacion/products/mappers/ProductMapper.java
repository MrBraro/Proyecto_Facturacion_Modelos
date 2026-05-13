package com.modelosgr86e1eq6.proyectofacturacion.products.mappers;

import com.modelosgr86e1eq6.proyectofacturacion.products.dto.ProductResponse;
import com.modelosgr86e1eq6.proyectofacturacion.products.entities.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir la entidad {@link Product} al DTO de salida {@link ProductResponse}.
 *
 * @author MrBraro
 */
@Component
public class ProductMapper {

    /**
     * Convierte un {@link Product} a su representación pública {@link ProductResponse}.
     *
     * @param product entidad a convertir; no debe ser {@code null}
     * @return DTO con los datos del producto
     */
    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getIdProduct());
        response.setCode(product.getCode());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        response.setStock(product.getStock());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
