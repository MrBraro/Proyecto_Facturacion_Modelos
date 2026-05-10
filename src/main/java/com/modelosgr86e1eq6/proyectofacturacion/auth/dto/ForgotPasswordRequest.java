package com.modelosgr86e1eq6.proyectofacturacion.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/** POST /auth/forgot-password */
@Data
public class ForgotPasswordRequest {
    @NotBlank
    @Email
    private String email;
}