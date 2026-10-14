package com.api.alquilaya.services;

import com.api.alquilaya.exceptions.ReglaNegocioException;
import com.api.alquilaya.models.OficinaModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.repositories.AlquilerRepository;
import com.api.alquilaya.repositories.MantenimientoRepository;
import com.api.alquilaya.repositories.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VehiculoService {

    public static final String DISPONIBLE = "DISPONIBLE";
    public static final String ALQUILADO = "ALQUILADO";
    public static final String MANTENIMIENTO = "MANTENIMIENTO";

    private final VehiculoRepository vehiculoRepository;
    private final AlquilerRepository alquilerRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final OficinaService oficinaService;

    public VehiculoService(VehiculoRepository vehiculoRepository,
                           AlquilerRepository alquilerRepository,
                           MantenimientoRepository mantenimientoRepository,
                           OficinaService oficinaService) {
        this.vehiculoRepository = vehiculoRepository;
        this.alquilerRepository = alquilerRepository;
        this.mantenimientoRepository = mantenimientoRepository;
        this.oficinaService = oficinaService;
    }

    public List<VehiculoModel> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    public VehiculoModel obtenerPorId(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vehículo no encontrado con id: " + id));
    }

    @Transactional
    public VehiculoModel crear(VehiculoModel vehiculo) {
        validarMatriculaUnica(vehiculo.getMatricula(), null);
        String estadoSolicitado = normalizarEstado(vehiculo.getEstado());
        vehiculo.setIdVehiculo(null);
        vehiculo.setAlquileres(null);
        vehiculo.setMantenimientos(null);
        vehiculo.setOficina(resolverOficina(vehiculo.getOficina()));
        // Un vehiculo nuevo no tiene alquileres: solo puede nacer disponible o en mantenimiento
        vehiculo.setEstado(MANTENIMIENTO.equals(estadoSolicitado) ? MANTENIMIENTO : DISPONIBLE);
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public VehiculoModel actualizar(Long id, VehiculoModel vehiculo) {
        VehiculoModel existente = obtenerPorId(id);
        validarMatriculaUnica(vehiculo.getMatricula(), id);
        String estadoSolicitado = normalizarEstado(vehiculo.getEstado());
        existente.setMarca(vehiculo.getMarca());
        existente.setModelo(vehiculo.getModelo());
        existente.setAnio(vehiculo.getAnio());
        existente.setMatricula(vehiculo.getMatricula());
        existente.setTarifaDiaria(vehiculo.getTarifaDiaria());
        existente.setOficina(resolverOficina(vehiculo.getOficina()));
        existente.setEstado(estadoSolicitado);
        // Si no se pide mantenimiento, el estado real (disponible o alquilado) depende de los alquileres
        existente.setEstado(calcularEstado(existente));
        return vehiculoRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        VehiculoModel existente = obtenerPorId(id);
        if (alquilerRepository.existsByVehiculo_IdVehiculo(id)) {
            throw new ReglaNegocioException("No se puede eliminar el vehículo porque tiene alquileres registrados");
        }
        if (mantenimientoRepository.existsByVehiculo_IdVehiculo(id)) {
            throw new ReglaNegocioException("No se puede eliminar el vehículo porque tiene mantenimientos registrados");
        }
        vehiculoRepository.delete(existente);
    }

    // Ajusta el estado del vehiculo segun sus alquileres en curso (MANTENIMIENTO no se toca)
    @Transactional
    public void sincronizarEstado(VehiculoModel vehiculo) {
        String nuevo = calcularEstado(vehiculo);
        if (!nuevo.equals(vehiculo.getEstado())) {
            vehiculo.setEstado(nuevo);
            vehiculoRepository.save(vehiculo);
        }
    }

    @Transactional
    public void sincronizarTodos() {
        vehiculoRepository.findAll().forEach(this::sincronizarEstado);
    }

    private String calcularEstado(VehiculoModel vehiculo) {
        if (MANTENIMIENTO.equalsIgnoreCase(vehiculo.getEstado())) {
            return MANTENIMIENTO;
        }
        if (vehiculo.getIdVehiculo() != null
                && alquilerRepository.contarActivos(vehiculo.getIdVehiculo(), LocalDate.now()) > 0) {
            return ALQUILADO;
        }
        return DISPONIBLE;
    }

    private String normalizarEstado(String estado) {
        String limpio = estado == null ? "" : estado.trim().toUpperCase();
        if (!limpio.equals(DISPONIBLE) && !limpio.equals(ALQUILADO) && !limpio.equals(MANTENIMIENTO)) {
            throw new ReglaNegocioException("Estado no válido: usa DISPONIBLE, ALQUILADO o MANTENIMIENTO");
        }
        return limpio;
    }

    private OficinaModel resolverOficina(OficinaModel oficina) {
        if (oficina == null || oficina.getIdOficina() == null) {
            throw new ReglaNegocioException("Selecciona una oficina");
        }
        return oficinaService.obtenerPorId(oficina.getIdOficina());
    }

    private void validarMatriculaUnica(String matricula, Long idActual) {
        vehiculoRepository.findByMatricula(matricula)
                .filter(otro -> !otro.getIdVehiculo().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un vehículo con esa matrícula");
                });
    }
}
