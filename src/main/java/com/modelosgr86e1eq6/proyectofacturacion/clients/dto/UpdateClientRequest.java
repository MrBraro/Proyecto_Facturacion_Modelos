package com.modelosgr86e1eq6.proyectofacturacion.clients.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@NoArgsConstructor
public class UpdateClientRequest {
 
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    private String name;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 12, message = "El NIT no puede superar los 12 caracteres")
    private String nit;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String email;
 
    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
    private String telephone;
 
    private String address;
}