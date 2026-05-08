package com.modelosgr86e1eq6.proyectofacturacion.auth.utils;

import com.modelosgr86e1eq6.users.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
 
@Component
public class JwtUtils {
 
    @Value("${app.jwt.secret}")
    private String secret;
 
    @Value("${app.jwt.expiration-hours:24}")
    private long expirationHours;
 
    private SecretKey getKey() {
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