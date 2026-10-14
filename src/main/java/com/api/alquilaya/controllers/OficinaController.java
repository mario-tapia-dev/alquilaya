package com.api.alquilaya.controllers;

import com.api.alquilaya.models.OficinaModel;
import com.api.alquilaya.services.OficinaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oficinas")
public class OficinaController {

    @Autowired
    private OficinaService oficinaService;

    @GetMapping
    public ResponseEntity<List<OficinaModel>> obtenerTodos() {
        return ResponseEntity.ok(oficinaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OficinaModel> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(oficinaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<OficinaModel> crear(@Valid @RequestBody OficinaModel oficina) {
        OficinaModel creada = oficinaService.crear(oficina);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OficinaModel> actualizar(@PathVariable Long id, @Valid @RequestBody OficinaModel oficina) {
        return ResponseEntity.ok(oficinaService.actualizar(id, oficina));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        oficinaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
