package com.modelosgr86e1eq6.proyectofacturacion.auth.utils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
 
@Component
public class JwtUtils {

    private static final int MIN_SECRET_LENGTH_BYTES = 32;
 
    @Value("${app.jwt.secret}")
    private String secret;
 
    @Value("${app.jwt.expiration-hours:24}")
    private long expirationHours;

    @PostConstruct //Es una anotación de Jakarta que indica que este método debe ejecutarse automáticamente después de que Spring haya creado el bean y realizado todas las inyecciones de dependencias
     private void validateConfiguration() {

        //implementa una estrategia "fail-fast" (fallar temprano)
        //Verifica que la propiedad app.jwt.secret exista y no esté vacía.
        //Si falta, lanza una excepción al iniciar la aplicación, no cuando alguien intenta generar/validar un token.
         if (secret == null || secret.isBlank()) {
             throw new IllegalStateException(
                     "Missing required property 'app.jwt.secret'. Configure it with a strong secret of at least "
                             + MIN_SECRET_LENGTH_BYTES + " bytes.");
         }
         int secretLengthBytes = secret.getBytes(StandardCharsets.UTF_8).length;
         if (secretLengthBytes < MIN_SECRET_LENGTH_BYTES) {
             throw new IllegalStateException(
                     "Invalid property 'app.jwt.secret': it must be at least "
                             + MIN_SECRET_LENGTH_BYTES + " bytes when encoded as UTF-8, but was "
                             + secretLengthBytes + " bytes.");
         }
     }
 
    private SecretKey getKey() {
        validateConfiguration();
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
 
    // ── Generación ────────────────────────────────────────────────────────────
 
    public String generateToken(User user) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationHours * 3600 * 1000);
 
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId",   user.getIdUser())
                .claim("role",     user.getRole().name())
                .claim("branchId", user.getIdBranch())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getKey())
                .compact();
    }
 
    public LocalDateTime getExpirationAsLocalDateTime() {
        return LocalDateTime.now().plusHours(expirationHours);
    }
 
    // ── Extracción de claims ──────────────────────────────────────────────────
 
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }
 
    public Integer extractUserId(String token) {
        return parseClaims(token).get("userId", Integer.class);
    }
 
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
 
    // ── Validación ────────────────────────────────────────────────────────────
 
    public boolean isTokenValid(String token, User user) {
        try {
            String email = extractEmail(token);
            return email.equals(user.getEmail()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
 
    public boolean isTokenExpired(String token) {
        Date expiry = parseClaims(token).getExpiration();
        return expiry.before(new Date());
    }
 
    // ── Interno ───────────────────────────────────────────────────────────────
 
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}