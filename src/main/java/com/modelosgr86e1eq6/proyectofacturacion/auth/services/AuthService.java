package com.modelosgr86e1eq6.proyectofacturacion.auth.services;
import com.modelosgr86e1eq6.proyectofacturacion.auth.dto.ChangePasswordRequest;
import com.modelosgr86e1eq6.proyectofacturacion.auth.dto.ForgotPasswordRequest;
import com.modelosgr86e1eq6.proyectofacturacion.auth.dto.LoginRequest;
import com.modelosgr86e1eq6.proyectofacturacion.auth.dto.LoginResponse;
import com.modelosgr86e1eq6.proyectofacturacion.auth.dto.RegisterRequest;
import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.SessionEntity;
import com.modelosgr86e1eq6.proyectofacturacion.auth.utils.JwtUtils;


import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
 
    private final UserRepository        userRepository;
    private final SessionRepository     sessionRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtils              jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final AuditService          auditService;
 
    // ── UserDetailsService (requerido por Spring Security) ────────────────────
 
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + email));
    }
 
    // ── RF-SEG-01: Registro de ADMIN ──────────────────────────────────────────
 
    @Transactional
    public void register(RegisterRequest request, String ip) {
        // Solo se permite un ADMIN activo en el sistema
        long adminCount = userRepository.countByRoleAndIsActiveTrue(Role.ADMIN);
        if (adminCount > 0) {
            throw new BusinessException(
                    "Ya existe un administrador activo. El registro está deshabilitado.");
        }
 
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo ya está registrado");
        }
 
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .isActive(true)
                .build();
 
        userRepository.save(user);
        auditService.log(AuditService.USER_CREATED, ip, "Registro inicial de ADMIN: " + user.getEmail());
    }
 
    // ── RF-SEG-02: Login ──────────────────────────────────────────────────────
 
    @Transactional
    public LoginResponse login(LoginRequest request, String ip) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            auditService.log(AuditService.LOGIN_FAIL, ip,
                    "Intento fallido para: " + request.getEmail());
            throw e;
        }
 
        User user = (User) loadUserByUsername(request.getEmail());
 
        String token = jwtUtils.generateToken(user);
 
        // Persistir sesión
        SessionEntity session = SessionEntity.builder()
                .user(user)
                .token(token)
                .expiresAt(jwtUtils.getExpirationAsLocalDateTime())
                .isActive(true)
                .build();
        sessionRepository.save(session);
 
        auditService.log(AuditService.LOGIN_OK, user, session, ip, null);
 
        return new LoginResponse(
                token,
                user.getRole().name(),
                user.getEmail(),
                user.getName(),
                user.getIdBranch()
        );
    }
 
    // ── RF-SEG-03: Logout ─────────────────────────────────────────────────────
 
    @Transactional
    public void logout(String token, String ip) {
        SessionEntity session = sessionRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada"));
 
        session.setActive(false);
        session.setRevokedAt(LocalDateTime.now());
        sessionRepository.save(session);
 
        auditService.log(AuditService.LOGOUT, session.getUser(), session, ip, null);
    }
 
    // ── RF-SEG-04: Recuperación de contraseña ─────────────────────────────────
 
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request, String ip) {
        // No revelar si el correo existe: registrar auditoría y salir sin error.
         User user = userRepository.findByEmail(request.getEmail()).orElse(null);
         if (user == null) {
             auditService.log(AuditService.PASSWORD_CHANGED, null, null, ip,
                     "Solicitud de recuperación de contraseña para correo no registrado");
             return;
         }
 
        // Generar token temporal reutilizando JwtUtils con expiración corta
        // En producción: enviar por email. En MVP: se imprime en logs.
        String resetToken = jwtUtils.generateToken(user);
 
        // Guardar como sesión temporal marcada
        SessionEntity session = SessionEntity.builder()
                .user(user)
                .token(resetToken)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .isActive(true)
                .build();
        sessionRepository.save(session);
 
        // MVP: log en consola en lugar de email real
        System.out.println("[MVP - RESET TOKEN] " + resetToken);
 
        auditService.log(AuditService.PASSWORD_CHANGED, user, null, ip,
                "Solicitud de recuperación de contraseña");
    }
 
    // ── RF-SEG-04: Reset de contraseña ────────────────────────────────────────
 
    @Transactional
    public void resetPassword(ResetPasswordRequest request, String ip) {
        SessionEntity session = sessionRepository.findByToken(request.getToken())
                .filter(SessionEntity::isActive)
                .orElseThrow(() -> new BusinessException(
                        "Token inválido o expirado"));
 
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("El token ha expirado");
        }
 
        User user = session.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
 
        // Revocar el token usado y todas las sesiones activas
        session.setActive(false);
        session.setRevokedAt(LocalDateTime.now());
        sessionRepository.save(session);
        sessionRepository.revokeAllByUserId(user.getIdUser());
 
        auditService.log(AuditService.PASSWORD_CHANGED, user, null, ip,
                "Contraseña reseteada via token");
    }
 
    // ── RF-SEG-05: Cambio de contraseña (usuario autenticado) ────────────────
 
    @Transactional
    public void changePassword(ChangePasswordRequest request,
                               User currentUser,
                               String currentToken,
                               String ip) {
 
        if (!passwordEncoder.matches(request.getCurrentPassword(),
                currentUser.getPasswordHash())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }
 
        currentUser.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword()));
        currentUser.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(currentUser);

        // Revocar todas las sesiones excepto la actual para forzar re-login
        // en otros dispositivos, pero mantener la sesión actual activa
        sessionRepository.revokeAllByUserIdExceptToken(currentUser.getIdUser(), currentToken);

        auditService.log(AuditService.PASSWORD_CHANGED, currentUser, null, ip,
                "Cambio de contraseña por el usuario");
    }
}