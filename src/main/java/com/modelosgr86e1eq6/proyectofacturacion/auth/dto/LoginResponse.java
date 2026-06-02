package com.modelosgr86e1eq6.proyectofacturacion.auth.dto;

import lombok.Data;

/** Respuesta de login */
@Data
public class LoginResponse {
    private String token;
    private String role;
    private String email;
    private String name;
 
    public LoginResponse(String token, String role, String email,
                         String name) {
        this.token    = token;
        this.role     = role;
        this.email    = email;
        this.name     = name;
    }
}