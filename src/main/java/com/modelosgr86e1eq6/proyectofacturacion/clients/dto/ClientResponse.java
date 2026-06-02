package com.modelosgr86e1eq6.proyectofacturacion.clients.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ClientResponse {
 
    private Integer idClient;
    private String nit;
    private String name;
    private String email;
    private String telephone;
    private String address;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}