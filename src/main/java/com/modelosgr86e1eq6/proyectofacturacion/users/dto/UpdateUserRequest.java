package com.modelosgr86e1eq6.proyectofacturacion.users.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** PUT /api/v1/users/{id} — actualizar datos de usuario */
@Data
public class UpdateUserRequest {
 
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
 
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String email;
}