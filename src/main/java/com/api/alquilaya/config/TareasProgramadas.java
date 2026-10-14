package com.api.alquilaya.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.api.alquilaya.services.VehiculoService;

@Configuration
@EnableScheduling
public class TareasProgramadas {

    private final VehiculoService vehiculoService;

    public TareasProgramadas(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    // Cada dia a las 00:05 se actualiza el estado de los vehiculos
    // (un alquiler que empieza o termina hoy cambia el estado del vehiculo sin que nadie lo edite)
    @Scheduled(cron = "0 5 0 * * *")
    public void sincronizarEstadosVehiculos() {
        vehiculoService.sincronizarTodos();
    }
}
