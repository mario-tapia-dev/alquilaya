package com.api.alquilaya.controllers.web;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.alquilaya.exceptions.ReglaNegocioException;
import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.services.AlquilerService;
import com.api.alquilaya.services.ClienteService;
import com.api.alquilaya.services.VehiculoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/alquileres")
public class AlquilerWebController {

    private final AlquilerService alquilerService;
    private final VehiculoService vehiculoService;
    private final ClienteService clienteService;

    public AlquilerWebController(AlquilerService alquilerService,
                                 VehiculoService vehiculoService,
                                 ClienteService clienteService) {
        this.alquilerService = alquilerService;
        this.vehiculoService = vehiculoService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alquileres", alquilerService.obtenerTodos());
        model.addAttribute("hoy", LocalDate.now());
        return "alquileres/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        AlquilerModel alquiler = new AlquilerModel();
        alquiler.setFechaAlquiler(LocalDate.now());
        alquiler.setFechaDevolucion(LocalDate.now().plusDays(1));
        return formulario(model, alquiler, "Nuevo alquiler", "/alquileres");
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("alquiler") AlquilerModel alquiler, BindingResult resultado,
                        Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, alquiler, "Nuevo alquiler", "/alquileres");
        }
        try {
            alquilerService.crear(alquiler);
            flash.addFlashAttribute("mensaje", "Alquiler registrado correctamente");
            return "redirect:/alquileres";
        } catch (ReglaNegocioException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(model, alquiler, "Nuevo alquiler", "/alquileres");
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            return formulario(model, alquilerService.obtenerPorId(id), "Editar alquiler", "/alquileres/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/alquileres";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("alquiler") AlquilerModel alquiler,
                             BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, alquiler, "Editar alquiler", "/alquileres/" + id);
        }
        try {
            alquilerService.actualizar(id, alquiler);
            flash.addFlashAttribute("mensaje", "Alquiler actualizado correctamente");
            return "redirect:/alquileres";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(model, alquiler, "Editar alquiler", "/alquileres/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/alquileres";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            alquilerService.eliminar(id);
            flash.addFlashAttribute("mensaje", "Alquiler eliminado correctamente");
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/alquileres";
    }

    private String formulario(Model model, AlquilerModel alquiler, String titulo, String accion) {
        // Los selects enlazan con vehiculo.idVehiculo y cliente.idCliente, asi que no pueden ser null
        if (alquiler.getVehiculo() == null) {
            alquiler.setVehiculo(new VehiculoModel());
        }
        if (alquiler.getCliente() == null) {
            alquiler.setCliente(new ClienteModel());
        }
        Long idSeleccionado = alquiler.getVehiculo().getIdVehiculo();

        // Los vehiculos en mantenimiento no se ofrecen (salvo el que ya tiene este alquiler)
        model.addAttribute("vehiculos", vehiculoService.obtenerTodos().stream()
                .filter(v -> !VehiculoService.MANTENIMIENTO.equalsIgnoreCase(v.getEstado())
                        || v.getIdVehiculo().equals(idSeleccionado))
                .toList());
        model.addAttribute("clientes", clienteService.obtenerTodos());
        model.addAttribute("alquiler", alquiler);
        model.addAttribute("titulo", titulo);
        model.addAttribute("accion", accion);
        return "alquileres/form";
    }
}
