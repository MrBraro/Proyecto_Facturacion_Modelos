package com.modelosgr86e1eq6.proyectofacturacion.auth.config;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
 
import java.util.List;

import com.modelosgr86e1eq6.proyectofacturacion.auth.filters.JwtAuthFilter;
import com.modelosgr86e1eq6.proyectofacturacion.auth.services.UserDetailsServiceImpl;

 
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // Habilita @PreAuthorize en controllers y services
@RequiredArgsConstructor
public class SecurityConfig {
 
    private final JwtAuthFilter      jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;  // ← impl concreta, no la interfaz
 
    // En desarrollo: "http://localhost:5500,http://127.0.0.1:5500"
    // En producción: "https://nuestrodominio.com"
    // Se define en application.properties → app.cors.allowed-origins
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // API REST — no necesita CSRF ni sesión HTTP
            .csrf(AbstractHttpConfigurer::disable)

            // CORS debe habilitarse aquí para que Spring Security
            // lo procese ANTES de evaluar autenticación.
            // Sin esto, los preflight OPTIONS son bloqueados con 403
            // antes de llegar al controller.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
 
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos — no requieren token
                .requestMatchers(
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/api/v1/auth/forgot-password",
                        "/api/v1/auth/reset-password"
                ).permitAll()
 
                // Solo ADMIN puede gestionar usuarios
                .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
 
                // Solo ADMIN puede ver auditoría
                .requestMatchers("/api/v1/audit/**").hasRole("ADMIN")
 
                // Todo lo demás requiere autenticación
                // El RBAC fino (ej: qué puede hacer EMPLOYEE) se maneja
                // con @PreAuthorize a nivel de método en cada controller
                .anyRequest().authenticated()
            )
 
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
 
        return http.build();
    }

    /**
     * Configuración CORS centralizada.
     *
     * allowedOrigins  → leído desde application.properties, separado por comas.
     *                   En dev: http://localhost:5500 (Live Server de VS Code)
     *                   En prod: el dominio real del frontend.
     *
     * allowedMethods  → los verbos que usa la API. OPTIONS es obligatorio
     *                   porque el navegador lo usa para el preflight check.
     *
     * allowedHeaders  → Authorization es necesario para enviar el JWT.
     *                   Content-Type para los POST con JSON.
     *
     * exposedHeaders  → permite que el frontend lea Authorization en la respuesta
     *                   si en algún endpoint se renueva el token.
     *
     * allowCredentials → false porque usamos JWT en header, no cookies de sesión.
     *
     * maxAge          → el navegador cachea el resultado del preflight 1 hora,
     *                   reduciendo requests innecesarios.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
 
        // Convertir el string de orígenes separado por comas a lista
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
 
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
 
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));
 
        config.setExposedHeaders(List.of("Authorization"));
 
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración a todos los endpoints
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
 
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
 
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}