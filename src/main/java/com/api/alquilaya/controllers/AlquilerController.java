package com.api.alquilaya.controllers;

import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.services.AlquilerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;

    @GetMapping
    public ResponseEntity<List<AlquilerModel>> obtenerTodos() {
        return ResponseEntity.ok(alquilerService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlquilerModel> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlquilerModel> crear(@Valid @RequestBody AlquilerModel alquiler) {
        AlquilerModel creado = alquilerService.crear(alquiler);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlquilerModel> actualizar(@PathVariable Long id, @Valid @RequestBody AlquilerModel alquiler) {
        return ResponseEntity.ok(alquilerService.actualizar(id, alquiler));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
