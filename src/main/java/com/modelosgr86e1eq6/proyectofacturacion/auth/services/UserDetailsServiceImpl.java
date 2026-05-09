package com.modelosgr86e1eq6.proyectofacturacion.auth.services;
 

import com.modelosgr86e1eq6.proyectofacturacion.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
 
/**
 * Implementación de UserDetailsService separada de AuthService
 * para evitar la referencia circular:
 *
 *   SecurityConfig → AuthenticationProvider → UserDetailsService
 *                                                      ↑
 *                                          (este bean, sin deps de Security)
 *
 * AuthService inyecta AuthenticationManager (que viene de SecurityConfig),
 * por eso no puede implementar UserDetailsService al mismo tiempo —
 * formaría un ciclo. Este bean rompe ese ciclo.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
 
    private final UserRepository userRepository;
 
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + email));
    }
}
 