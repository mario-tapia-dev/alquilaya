package com.api.alquilaya.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.alquilaya.models.UsuariosModel;
import com.api.alquilaya.repositories.IUsuariosRepository;

@Service
public class UsuariosService {
    
    private final IUsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor de la clase UsuarioService
    public UsuariosService(IUsuariosRepository usuariosRepository, PasswordEncoder passwordEncoder) {
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Metodo para crear un usuario
    public UsuariosModel crearUsuario(String email, String password, String rol) {

        if (usuariosRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        UsuariosModel usuario = new UsuariosModel();

        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setActivo(true);

        return usuariosRepository.save(usuario);
    }

    // Metodo para obtener un usuario por email 
    public Optional<UsuariosModel> obtenerUsuarioPorEmail(String email) {
        return usuariosRepository.findByEmail(email);
    }

}
