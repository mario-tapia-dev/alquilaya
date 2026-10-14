package com.api.alquilaya.controllers.web;

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
import com.api.alquilaya.models.OficinaModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.services.OficinaService;
import com.api.alquilaya.services.VehiculoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoWebController {

    private final VehiculoService vehiculoService;
    private final OficinaService oficinaService;

    public VehiculoWebController(VehiculoService vehiculoService, OficinaService oficinaService) {
        this.vehiculoService = vehiculoService;
        this.oficinaService = oficinaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vehiculos", vehiculoService.obtenerTodos());
        return "vehiculos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        VehiculoModel vehiculo = new VehiculoModel();
        vehiculo.setEstado(VehiculoService.DISPONIBLE);
        return formulario(model, vehiculo, "Nuevo vehículo", "/vehiculos");
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("vehiculo") VehiculoModel vehiculo, BindingResult resultado,
                        Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, vehiculo, "Nuevo vehículo", "/vehiculos");
        }
        try {
            vehiculoService.crear(vehiculo);
            flash.addFlashAttribute("mensaje", "Vehículo creado correctamente");
            return "redirect:/vehiculos";
        } catch (ReglaNegocioException | NoSuchElementException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(model, vehiculo, "Nuevo vehículo", "/vehiculos");
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            return formulario(model, vehiculoService.obtenerPorId(id), "Editar vehículo", "/vehiculos/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/vehiculos";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("vehiculo") VehiculoModel vehiculo,
                             BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, vehiculo, "Editar vehículo", "/vehiculos/" + id);
        }
        try {
            vehiculoService.actualizar(id, vehiculo);
            flash.addFlashAttribute("mensaje", "Vehículo actualizado correctamente");
            return "redirect:/vehiculos";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(model, vehiculo, "Editar vehículo", "/vehiculos/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/vehiculos";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            vehiculoService.eliminar(id);
            flash.addFlashAttribute("mensaje", "Vehículo eliminado correctamente");
        } catch (ReglaNegocioException | NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vehiculos";
    }

    private String formulario(Model model, VehiculoModel vehiculo, String titulo, String accion) {
        // El select de oficina enlaza con oficina.idOficina, asi que nunca debe ser null
        if (vehiculo.getOficina() == null) {
            vehiculo.setOficina(new OficinaModel());
        }
        model.addAttribute("vehiculo", vehiculo);
        model.addAttribute("oficinas", oficinaService.obtenerTodos());
        model.addAttribute("titulo", titulo);
        model.addAttribute("accion", accion);
        return "vehiculos/form";
    }
}
