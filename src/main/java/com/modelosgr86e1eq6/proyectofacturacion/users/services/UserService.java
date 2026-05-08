package com.modelosgr86e1eq6.proyectofacturacion.users.services;

import com.modelosgr86e1eq6.proyectofacturacion.users.entities.Role;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
import com.modelosgr86e1eq6.proyectofacturacion.auth.repositories.SessionRepository;
import com.modelosgr86e1eq6.proyectofacturacion.users.repositories.UserRepository;
import com.modelosgr86e1eq6.proyectofacturacion.auth.services.AuditService;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.BusinessException;
import com.modelosgr86e1eq6.proyectofacturacion.common.exception.ResourceNotFoundException;
import com.modelosgr86e1eq6.proyectofacturacion.users.dto.CreateUserRequest;
import com.modelosgr86e1eq6.proyectofacturacion.users.dto.UpdateUserRequest;
import com.modelosgr86e1eq6.proyectofacturacion.users.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@RequiredArgsConstructor
public class UserService {
 
    private final UserRepository    userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder   passwordEncoder;
    private final AuditService      auditService;
 
    // ── RF-SEG-08: Listar usuarios ────────────────────────────────────────────
 
    public Page<UserSummaryResponse> findAll(Role role, Integer branchId,
                                             Pageable pageable) {
        return userRepository
                .findByFilters(role, branchId, pageable)
                .map(this::toSummary);
    }
 
    // ── RF-SEG-08: Detalle de usuario ─────────────────────────────────────────
 
    public UserSummaryResponse findById(Integer id) {
        return toSummary(getOrThrow(id));
    }
 
    // ── RF-SEG-10: Crear empleado (solo ADMIN) ────────────────────────────────
 
    @Transactional
    public UserSummaryResponse createEmployee(CreateUserRequest request,
                                              UserEntity admin,
                                              String ip) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo ya está en uso");
        }
 
        UserEntity employee = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.EMPLOYEE)
                .idBranch(request.getBranchId())
                .isActive(true)
                .build();
 
        userRepository.save(employee);
        auditService.log(AuditService.USER_CREATED, admin, null, ip,
                "Empleado creado: " + employee.getEmail());
 
        return toSummary(employee);
    }
 
    // ── RF-SEG-08: Actualizar usuario ─────────────────────────────────────────
 
    @Transactional
    public UserSummaryResponse update(Integer id, UpdateUserRequest request,
                                      UserEntity admin, String ip) {
        UserEntity user = getOrThrow(id);
 
        // Si cambia el correo, verificar que no esté en uso
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo ya está en uso");
        }
 
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setIdBranch(request.getBranchId());
        userRepository.save(user);
 
        auditService.log(AuditService.USER_UPDATED, admin, null, ip,
                "Usuario actualizado: " + user.getIdUser());
 
        return toSummary(user);
    }
 
    // ── RF-SEG-08: Desactivar usuario ─────────────────────────────────────────
 
    @Transactional
    public void deactivate(Integer id, UserEntity admin, String ip) {
        UserEntity user = getOrThrow(id);
 
        // No permitir desactivar al único ADMIN activo
        if (user.getRole() == Role.ADMIN) {
            long activeAdmins = userRepository.countByRoleAndIsActiveTrue(Role.ADMIN);
            if (activeAdmins <= 1) {
                throw new BusinessException(
                        "No se puede desactivar el único administrador activo");
            }
        }
 
        user.setActive(false);
        userRepository.save(user);
 
        // Revocar todas sus sesiones activas
        sessionRepository.revokeAllByUserId(user.getIdUser());
 
        auditService.log(AuditService.USER_DEACTIVATED, admin, null, ip,
                "Usuario desactivado: " + user.getIdUser());
    }
 
    // ── RF-SEG-08: Reactivar usuario ──────────────────────────────────────────
 
    @Transactional
    public void activate(Integer id, UserEntity admin, String ip) {
        UserEntity user = getOrThrow(id);
        user.setActive(true);
        userRepository.save(user);
 
        auditService.log(AuditService.USER_ACTIVATED, admin, null, ip,
                "Usuario activado: " + user.getIdUser());
    }
 
    // ── Interno ───────────────────────────────────────────────────────────────
 
    private UserEntity getOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + id));
    }
 
    private UserSummaryResponse toSummary(UserEntity user) {
        UserSummaryResponse dto = new UserSummaryResponse();
        dto.setId(user.getIdUser());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setBranchId(user.getIdBranch());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        
        return dto;
    }
}