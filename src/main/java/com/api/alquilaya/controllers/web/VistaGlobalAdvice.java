package com.api.alquilaya.controllers.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Datos que necesitan todas las pantallas (menu segun el rol y nombre del usuario)
@ControllerAdvice(basePackages = "com.api.alquilaya.controllers.web")
public class VistaGlobalAdvice {

    @ModelAttribute("esAdmin")
    public boolean esAdmin() {
        Authentication auth = autenticacion();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @ModelAttribute("usuarioActual")
    public String usuarioActual() {
        Authentication auth = autenticacion();
        return auth == null ? "" : auth.getName();
    }

    private Authentication autenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return null;
        }
        return auth;
    }
}
