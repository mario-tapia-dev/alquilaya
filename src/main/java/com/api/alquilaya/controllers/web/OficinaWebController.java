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
import com.api.alquilaya.services.OficinaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/oficinas")
public class OficinaWebController {

    private final OficinaService oficinaService;

    public OficinaWebController(OficinaService oficinaService) {
        this.oficinaService = oficinaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("oficinas", oficinaService.obtenerTodos());
        return "oficinas/lista";
    }

    @GetMapping("/nueva")
    public String nuevo(Model model) {
        return formulario(model, new OficinaModel(), "Nueva oficina", "/oficinas");
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("oficina") OficinaModel oficina, BindingResult resultado,
                        Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, oficina, "Nueva oficina", "/oficinas");
        }
        oficinaService.crear(oficina);
        flash.addFlashAttribute("mensaje", "Oficina creada correctamente");
        return "redirect:/oficinas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            return formulario(model, oficinaService.obtenerPorId(id), "Editar oficina", "/oficinas/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/oficinas";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("oficina") OficinaModel oficina,
                             BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, oficina, "Editar oficina", "/oficinas/" + id);
        }
        try {
            oficinaService.actualizar(id, oficina);
            flash.addFlashAttribute("mensaje", "Oficina actualizada correctamente");
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/oficinas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            oficinaService.eliminar(id);
            flash.addFlashAttribute("mensaje", "Oficina eliminada correctamente");
        } catch (ReglaNegocioException | NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/oficinas";
    }

    private String formulario(Model model, OficinaModel oficina, String titulo, String accion) {
        model.addAttribute("oficina", oficina);
        model.addAttribute("titulo", titulo);
        model.addAttribute("accion", accion);
        return "oficinas/form";
    }
}
