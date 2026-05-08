package com.modelosgr86e1eq6.proyectofacturacion.users.dto;

import com.modelosgr86e1eq6.proyectofacturacion.users.entities.Role;
import lombok.Data;
import java.time.LocalDateTime;

 
/** Vista resumida de usuario para listados */
@Data
public class UserSummaryResponse {
    private Integer       id;
    private String        name;
    private String        email;
    private Role          role;
    private Integer       branchId;
    private boolean       active;
    private LocalDateTime createdAt;
}