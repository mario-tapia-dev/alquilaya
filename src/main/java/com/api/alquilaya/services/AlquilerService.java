package com.api.alquilaya.services;

import com.api.alquilaya.exceptions.ReglaNegocioException;
import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.repositories.AlquilerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final VehiculoService vehiculoService;
    private final ClienteService clienteService;

    public AlquilerService(AlquilerRepository alquilerRepository,
                           VehiculoService vehiculoService,
                           ClienteService clienteService) {
        this.alquilerRepository = alquilerRepository;
        this.vehiculoService = vehiculoService;
        this.clienteService = clienteService;
    }

    public List<AlquilerModel> obtenerTodos() {
        return alquilerRepository.findAll();
    }

    public AlquilerModel obtenerPorId(Long id) {
        return alquilerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Alquiler no encontrado con id: " + id));
    }

    @Transactional
    public AlquilerModel crear(AlquilerModel datos) {
        AlquilerModel nuevo = new AlquilerModel();
        aplicarDatos(nuevo, datos, null);
        AlquilerModel guardado = alquilerRepository.save(nuevo);
        vehiculoService.sincronizarEstado(guardado.getVehiculo());
        return guardado;
    }

    @Transactional
    public AlquilerModel actualizar(Long id, AlquilerModel datos) {
        AlquilerModel existente = obtenerPorId(id);
        VehiculoModel vehiculoAnterior = existente.getVehiculo();
        aplicarDatos(existente, datos, id);
        AlquilerModel guardado = alquilerRepository.save(existente);
        vehiculoService.sincronizarEstado(guardado.getVehiculo());
        if (!vehiculoAnterior.getIdVehiculo().equals(guardado.getVehiculo().getIdVehiculo())) {
            vehiculoService.sincronizarEstado(vehiculoAnterior);
        }
        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        AlquilerModel existente = obtenerPorId(id);
        VehiculoModel vehiculo = existente.getVehiculo();
        alquilerRepository.delete(existente);
        vehiculoService.sincronizarEstado(vehiculo);
    }

    // Valida las reglas del alquiler y copia los datos al registro destino.
    // Todas las validaciones ocurren antes de modificar el destino.
    private void aplicarDatos(AlquilerModel destino, AlquilerModel datos, Long idActual) {
        if (datos.getVehiculo() == null || datos.getVehiculo().getIdVehiculo() == null) {
            throw new ReglaNegocioException("Selecciona un vehículo");
        }
        if (datos.getCliente() == null || datos.getCliente().getIdCliente() == null) {
            throw new ReglaNegocioException("Selecciona un cliente");
        }

        LocalDate inicio = datos.getFechaAlquiler();
        LocalDate fin = datos.getFechaDevolucion();
        if (inicio == null || fin == null) {
            throw new ReglaNegocioException("Las fechas de alquiler y devolución son obligatorias");
        }
        if (!fin.isAfter(inicio)) {
            throw new ReglaNegocioException("La fecha de devolución debe ser posterior a la fecha de alquiler");
        }

        // Se cargan desde la base de datos: del JSON o del formulario solo llega el id
        VehiculoModel vehiculo = vehiculoService.obtenerPorId(datos.getVehiculo().getIdVehiculo());
        ClienteModel cliente = clienteService.obtenerPorId(datos.getCliente().getIdCliente());

        boolean mismoVehiculo = destino.getVehiculo() != null
                && destino.getVehiculo().getIdVehiculo().equals(vehiculo.getIdVehiculo());
        if (!mismoVehiculo && VehiculoService.MANTENIMIENTO.equalsIgnoreCase(vehiculo.getEstado())) {
            throw new ReglaNegocioException("El vehículo está en mantenimiento y no se puede alquilar");
        }

        long cruces = alquilerRepository.contarSolapamientos(
                vehiculo.getIdVehiculo(), inicio, fin, idActual == null ? -1L : idActual);
        if (cruces > 0) {
            throw new ReglaNegocioException("El vehículo ya tiene un alquiler que se cruza con esas fechas");
        }

        destino.setVehiculo(vehiculo);
        destino.setCliente(cliente);
        destino.setFechaAlquiler(inicio);
        destino.setFechaDevolucion(fin);
        destino.setPrecioTotal(calcularPrecioTotal(vehiculo, inicio, fin));
    }

    // Precio total = tarifa diaria del vehiculo x dias de alquiler (minimo 1 por la validacion de fechas)
    private BigDecimal calcularPrecioTotal(VehiculoModel vehiculo, LocalDate inicio, LocalDate fin) {
        long dias = ChronoUnit.DAYS.between(inicio, fin);
        return vehiculo.getTarifaDiaria().multiply(BigDecimal.valueOf(dias));
    }
}
