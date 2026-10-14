package com.api.alquilaya.controllers.web;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.api.alquilaya.repositories.AlquilerRepository;
import com.api.alquilaya.repositories.ClienteRepository;
import com.api.alquilaya.repositories.OficinaRepository;
import com.api.alquilaya.repositories.VehiculoRepository;
import com.api.alquilaya.services.VehiculoService;

@Controller
public class InicioWebController {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final OficinaRepository oficinaRepository;
    private final AlquilerRepository alquilerRepository;

    public InicioWebController(VehiculoRepository vehiculoRepository,
                               ClienteRepository clienteRepository,
                               OficinaRepository oficinaRepository,
                               AlquilerRepository alquilerRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
        this.oficinaRepository = oficinaRepository;
        this.alquilerRepository = alquilerRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("totalVehiculos", vehiculoRepository.count());
        model.addAttribute("disponibles", vehiculoRepository.countByEstado(VehiculoService.DISPONIBLE));
        model.addAttribute("alquilados", vehiculoRepository.countByEstado(VehiculoService.ALQUILADO));
        model.addAttribute("enMantenimiento", vehiculoRepository.countByEstado(VehiculoService.MANTENIMIENTO));
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalOficinas", oficinaRepository.count());
        model.addAttribute("alquileresActivos", alquilerRepository.contarActivosGlobal(LocalDate.now()));
        return "inicio";
    }
}
