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
import com.api.alquilaya.models.MantenimientoModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.services.MantenimientoService;
import com.api.alquilaya.services.VehiculoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/mantenimientos")
public class MantenimientoWebController {

    private final MantenimientoService mantenimientoService;
    private final VehiculoService vehiculoService;

    public MantenimientoWebController(MantenimientoService mantenimientoService, VehiculoService vehiculoService) {
        this.mantenimientoService = mantenimientoService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mantenimientos", mantenimientoService.obtenerTodos());
        return "mantenimientos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        MantenimientoModel mantenimiento = new MantenimientoModel();
        mantenimiento.setFechaMantenimiento(LocalDate.now());
        return formulario(model, mantenimiento, "Nuevo mantenimiento", "/mantenimientos");
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("mantenimiento") MantenimientoModel mantenimiento,
                        BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, mantenimiento, "Nuevo mantenimiento", "/mantenimientos");
        }
        try {
            mantenimientoService.crear(mantenimiento);
            flash.addFlashAttribute("mensaje", "Mantenimiento registrado correctamente");
            return "redirect:/mantenimientos";
        } catch (ReglaNegocioException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(model, mantenimiento, "Nuevo mantenimiento", "/mantenimientos");
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            return formulario(model, mantenimientoService.obtenerPorId(id), "Editar mantenimiento",
                    "/mantenimientos/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/mantenimientos";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("mantenimiento") MantenimientoModel mantenimiento,
                             BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, mantenimiento, "Editar mantenimiento", "/mantenimientos/" + id);
        }
        try {
            mantenimientoService.actualizar(id, mantenimiento);
            flash.addFlashAttribute("mensaje", "Mantenimiento actualizado correctamente");
            return "redirect:/mantenimientos";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(model, mantenimiento, "Editar mantenimiento", "/mantenimientos/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/mantenimientos";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            mantenimientoService.eliminar(id);
            flash.addFlashAttribute("mensaje", "Mantenimiento eliminado correctamente");
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/mantenimientos";
    }

    private String formulario(Model model, MantenimientoModel mantenimiento, String titulo, String accion) {
        if (mantenimiento.getVehiculo() == null) {
            mantenimiento.setVehiculo(new VehiculoModel());
        }
        model.addAttribute("mantenimiento", mantenimiento);
        model.addAttribute("vehiculos", vehiculoService.obtenerTodos());
        model.addAttribute("titulo", titulo);
        model.addAttribute("accion", accion);
        return "mantenimientos/form";
    }
}
