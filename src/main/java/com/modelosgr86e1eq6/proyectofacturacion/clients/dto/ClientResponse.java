package com.modelosgr86e1eq6.proyectofacturacion.clients.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@NoArgsConstructor
public class ClientResponse {
 
    private String idClient;
    private String name;
    private String email;
    private String telephone;
    private String address;
    private boolean isActive;
    private String createdAt;
    private String updatedAt;
}