package com.modelosgr86e1eq6.proyectofacturacion.clients.dto;

import org.springframework.stereotype.Component;
import com.modelosgr86e1eq6.proyectofacturacion.clients.entities.Client;
 
@Component
public class ClientMapper {
 
    public ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setIdClient(client.getIdClient().toString());
        response.setName(client.getName());
        response.setEmail(client.getEmail());
        response.setTelephone(client.getTelephone());
        response.setAddress(client.getAddress());
        response.setActive(client.isActive());
        response.setCreatedAt(client.getCreatedAt().toString());
        response.setUpdatedAt(client.getUpdatedAt().toString());
        return response;
    }
 
    public Client toEntity(CreateClientRequest request) {
        return Client.builder()
                .name(request.getName())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .address(request.getAddress())
                .build();
    }
 
    public void updateEntity(Client client, UpdateClientRequest request) {
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setTelephone(request.getTelephone());
        client.setAddress(request.getAddress());
    }
}