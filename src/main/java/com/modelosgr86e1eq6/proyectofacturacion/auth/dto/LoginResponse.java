package com.modelosgr86e1eq6.proyectofacturacion.auth.dto;

import lombok.Data;

/** Respuesta de login */
@Data
public class LoginResponse {
    private String token;
    private String role;
    private String email;
    private String name;
    private Integer branchId;
 
    public LoginResponse(String token, String role, String email,
                         String name, Integer branchId) {
        this.token    = token;
        this.role     = role;
        this.email    = email;
        this.name     = name;
        this.branchId = branchId;
    }
}