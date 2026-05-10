package com.modelosgr86e1eq6.proyectofacturacion.users.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** POST /api/v1/users — crear empleado */
@Data
public class CreateUserRequest {
 
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
 
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    private String email;
 
    @NotBlank(message = "La contraseña temporal es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
 
    // Solo ADMIN puede asignar sucursal al crear empleado
    private Integer branchId;
}