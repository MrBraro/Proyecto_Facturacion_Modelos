package com.modelosgr86e1eq6.proyectofacturacion.auth.filters;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.modelosgr86e1eq6.proyectofacturacion.auth.repositories.SessionRepository;
import com.modelosgr86e1eq6.proyectofacturacion.auth.utils.JwtUtils;
import com.modelosgr86e1eq6.proyectofacturacion.users.entities.User;
import com.modelosgr86e1eq6.proyectofacturacion.users.repositories.UserRepository;
import com.modelosgr86e1eq6.proyectofacturacion.auth.entities.Session;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
 
    private final JwtUtils            jwtUtils;
    private final UserRepository      userRepository;
    private final SessionRepository   sessionRepository;
 
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
 
        final String authHeader = request.getHeader("Authorization");
 
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
 
        final String token = authHeader.substring(7).trim();
 
        try {
            final String email = jwtUtils.extractEmail(token);
 
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
 
                User user = userRepository.findByEmail(email).orElse(null);
                if (user == null) {
                    filterChain.doFilter(request, response);
                    return;
                }
 
                // ANTES: .map(s -> s.isActive()).orElse(false)
                // Solo verificaba revocación explícita, ignoraba expiración por tiempo.
                //
                // AHORA: session.getState().isValid(session)
                // ActiveSessionState   → verifica expiración por tiempo (lazy expiry),
                //                        actualiza status a EXPIRED si corresponde y retorna false
                // RevokedSessionState  → retorna false directamente
                // ExpiredSessionState  → retorna false directamente
                //
                // Si la sesión no existe en BD, orElse devuelve null y sessionValid = false
                boolean sessionValid = sessionRepository.findByToken(token)
                        .map(this::evaluateSessionState)
                        .orElse(false);
 
                if (jwtUtils.isTokenValid(token, user) && sessionValid) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ignored) {
            // Token malformado o expirado — Spring Security rechazará la request
        }
 
        filterChain.doFilter(request, response);
    }
 
    /**
     * Evalúa el estado de la sesión mediante el patrón State.
     * Si la sesión estaba ACTIVE pero expiró por tiempo, ActiveSessionState
     * la transiciona a EXPIRED lazy y persiste el cambio.
     */
    private boolean evaluateSessionState(Session session) {
        boolean valid = session.getState().isValid(session);
 
        // Si isValid() detectó una expiración lazy (ACTIVE → EXPIRED),
        // persistir el cambio de status en BD para que quede consistente.
        if (!valid && session.getStatus() != null) {
            sessionRepository.save(session);
        }
 
        return valid;
    }
}