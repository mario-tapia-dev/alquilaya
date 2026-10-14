package com.api.alquilaya.services;

import com.api.alquilaya.exceptions.ReglaNegocioException;
import com.api.alquilaya.models.MantenimientoModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.repositories.MantenimientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final VehiculoService vehiculoService;

    public MantenimientoService(MantenimientoRepository mantenimientoRepository, VehiculoService vehiculoService) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.vehiculoService = vehiculoService;
    }

    public List<MantenimientoModel> obtenerTodos() {
        return mantenimientoRepository.findAll();
    }

    public MantenimientoModel obtenerPorId(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mantenimiento no encontrado con id: " + id));
    }

    public MantenimientoModel crear(MantenimientoModel mantenimiento) {
        mantenimiento.setIdMantenimiento(null);
        mantenimiento.setVehiculo(resolverVehiculo(mantenimiento.getVehiculo()));
        return mantenimientoRepository.save(mantenimiento);
    }

    public MantenimientoModel actualizar(Long id, MantenimientoModel mantenimiento) {
        MantenimientoModel existente = obtenerPorId(id);
        existente.setVehiculo(resolverVehiculo(mantenimiento.getVehiculo()));
        existente.setFechaMantenimiento(mantenimiento.getFechaMantenimiento());
        existente.setDescripcion(mantenimiento.getDescripcion());
        existente.setCosto(mantenimiento.getCosto());
        return mantenimientoRepository.save(existente);
    }

    public void eliminar(Long id) {
        MantenimientoModel existente = obtenerPorId(id);
        mantenimientoRepository.delete(existente);
    }

    private VehiculoModel resolverVehiculo(VehiculoModel vehiculo) {
        if (vehiculo == null || vehiculo.getIdVehiculo() == null) {
            throw new ReglaNegocioException("Selecciona un vehículo");
        }
        return vehiculoService.obtenerPorId(vehiculo.getIdVehiculo());
    }
}
