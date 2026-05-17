package com.modelosgr86e1eq6.proyectofacturacion.clients.services;

import com.modelosgr86e1eq6.proyectofacturacion.clients.dto.ClientMapper;
import com.modelosgr86e1eq6.proyectofacturacion.clients.dto.ClientResponse;
import com.modelosgr86e1eq6.proyectofacturacion.clients.dto.CreateClientRequest;
import com.modelosgr86e1eq6.proyectofacturacion.clients.dto.UpdateClientRequest;
import com.modelosgr86e1eq6.proyectofacturacion.clients.entities.Client;
import com.modelosgr86e1eq6.proyectofacturacion.clients.repositories.ClientRepository;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@RequiredArgsConstructor
public class  ClientService {
 
    private final ClientRepository clientRepository;
    private final ClientMapper     clientMapper;
 
    // ── RF-07 ─────────────────────────────────────────────────────────────
    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Ya existe un cliente registrado con el correo: " + request.getEmail());
        }
        Client saved = clientRepository.save(clientMapper.toEntity(request));
        return clientMapper.toResponse(saved);
    }
 
    // ── RF-08 ─────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Page<ClientResponse> findAll(Pageable pageable) {
        return clientRepository
                .findAllByIsActiveTrue(pageable)
                .map(clientMapper::toResponse);
    }
 
    // ── RF-09 ─────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ClientResponse findById(Integer idClient) {
        return clientMapper.toResponse(getActiveClientOrThrow(idClient));
    }
 
    // ── RF-10 ─────────────────────────────────────────────────────────────
    @Transactional
    public ClientResponse update(Integer idClient, UpdateClientRequest request) {
        Client client = getActiveClientOrThrow(idClient);
 
        if (clientRepository.existsByEmailAndIdClientNot(request.getEmail(), idClient)) {
            throw new BusinessException(
                    "El correo " + request.getEmail() + " ya está en uso por otro cliente");
        }
 
        clientMapper.updateEntity(client, request);
        return clientMapper.toResponse(clientRepository.save(client));
    }
 
    // ── RF-11 ─────────────────────────────────────────────────────────────
    @Transactional
    public void delete(Integer idClient) {
        Client client = getActiveClientOrThrow(idClient);
        client.setActive(false);
        clientRepository.save(client);
    }
 
    // ── Helper ────────────────────────────────────────────────────────────
    private Client getActiveClientOrThrow(Integer idClient) {
        return clientRepository
                .findByIdClientAndIsActiveTrue(idClient)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + idClient));
    }
}