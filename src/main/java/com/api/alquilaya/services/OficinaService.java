package com.api.alquilaya.services;

import com.api.alquilaya.exceptions.ReglaNegocioException;
import com.api.alquilaya.models.OficinaModel;
import com.api.alquilaya.repositories.OficinaRepository;
import com.api.alquilaya.repositories.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OficinaService {

    private final OficinaRepository oficinaRepository;
    private final VehiculoRepository vehiculoRepository;

    public OficinaService(OficinaRepository oficinaRepository, VehiculoRepository vehiculoRepository) {
        this.oficinaRepository = oficinaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<OficinaModel> obtenerTodos() {
        return oficinaRepository.findAll();
    }

    public OficinaModel obtenerPorId(Long id) {
        return oficinaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Oficina no encontrada con id: " + id));
    }

    public OficinaModel crear(OficinaModel oficina) {
        // Se ignora cualquier id o lista que venga en el cuerpo: es un registro nuevo
        oficina.setIdOficina(null);
        oficina.setVehiculos(null);
        return oficinaRepository.save(oficina);
    }

    public OficinaModel actualizar(Long id, OficinaModel oficina) {
        OficinaModel existente = obtenerPorId(id);
        existente.setNombreOficina(oficina.getNombreOficina());
        existente.setDireccion(oficina.getDireccion());
        existente.setCiudad(oficina.getCiudad());
        existente.setRegion(oficina.getRegion());
        return oficinaRepository.save(existente);
    }

    public void eliminar(Long id) {
        OficinaModel existente = obtenerPorId(id);
        if (vehiculoRepository.existsByOficina_IdOficina(id)) {
            throw new ReglaNegocioException("No se puede eliminar la oficina porque tiene vehículos asignados");
        }
        oficinaRepository.delete(existente);
    }
}
