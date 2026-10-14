package com.api.alquilaya.config;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.api.alquilaya.repositories.IUsuariosRepository;

@Configuration 
public class SecurityConfig {
    
    // Bean para el codificador de contraseñas
    @Bean 
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean para el servicio de detalles del usuario
    @Bean 
    public UserDetailsService userDetailsService(IUsuariosRepository usuariosRepository) {
        
        return email -> usuariosRepository.findByEmail(email).map(usuario -> User.withUsername(usuario.getEmail())
            .password(usuario.getPassword())
            .roles(usuario.getRol())
            // Un usuario activo NO esta deshabilitado
            .disabled(!usuario.isActivo())
            .build())
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // Bean para la configuracion de la cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Las peticiones a la API con cabecera "Authorization: Basic ..." no usan cookies de sesion,
        // por lo que no necesitan token CSRF. Las pantallas (formularios) si lo usan.
        RequestMatcher peticionBasic = (HttpServletRequest request) -> {
            String cabecera = request.getHeader("Authorization");
            return cabecera != null && cabecera.startsWith("Basic ");
        };

        http
            .csrf(csrf -> csrf.ignoringRequestMatchers(peticionBasic))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/error").permitAll()

                // API REST
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/vehiculos/**").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers("/api/vehiculos/**", "/api/oficinas/**", "/api/mantenimientos/**").hasRole("ADMIN")
                .requestMatchers("/api/**").hasAnyRole("ADMIN", "OPERADOR")

                // Pantallas: el operador solo gestiona clientes y alquileres
                .requestMatchers("/clientes/**", "/alquileres/**").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers("/vehiculos/**", "/oficinas/**", "/mantenimientos/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
