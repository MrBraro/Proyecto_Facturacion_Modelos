package com.modelosgr86e1eq6.proyectofacturacion.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** POST /auth/reset-password */
@Data
public class ResetPasswordRequest {
    @NotBlank
    private String token;
 
    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String newPassword;
}