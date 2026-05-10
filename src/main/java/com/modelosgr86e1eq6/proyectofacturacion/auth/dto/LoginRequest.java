package com.modelosgr86e1eq6.proyectofacturacion.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

/** POST /auth/login */
@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;
 
    @NotBlank
    private String password;
}