package com.modelosgr86e1eq6.proyectofacturacion.users.dto;

import java.time.LocalDateTime;

import com.modelosgr86e1eq6.proyectofacturacion.users.entities.Role;

import lombok.Data;

 
/** Vista resumida de usuario para listados */
@Data
public class UserSummaryResponse {
    private Integer       id;
    private String        name;
    private String        email;
    private Role          role;
    private boolean       active;
    private LocalDateTime createdAt;
}