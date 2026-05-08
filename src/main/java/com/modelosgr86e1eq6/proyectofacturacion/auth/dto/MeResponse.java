package com.modelosgr86e1eq6.proyectofacturacion.auth.dto;

import com.cocobongo.cerveceria.users.entities.Role;
import lombok.Data;
/** GET /auth/me */
@Data
public class MeResponse {
    private Integer id;
    private String  name;
    private String  email;
    private Role    role;
    private Integer branchId;
    private boolean active;
}