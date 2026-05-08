package com.modelosgr86e1eq6.proyectofacturacion.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** PUT /auth/change-password */
@Data
public class ChangePasswordRequest {
    @NotBlank
    private String currentPassword;
 
    @NotBlank
    @Size(min = 8)
    private String newPassword;
}
