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
import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.services.ClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteWebController {

    private final ClienteService clienteService;

    public ClienteWebController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.obtenerTodos());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        return formulario(model, new ClienteModel(), "Nuevo cliente", "/clientes");
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("cliente") ClienteModel cliente, BindingResult resultado,
                        Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, cliente, "Nuevo cliente", "/clientes");
        }
        try {
            clienteService.crear(cliente);
            flash.addFlashAttribute("mensaje", "Cliente creado correctamente");
            return "redirect:/clientes";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(model, cliente, "Nuevo cliente", "/clientes");
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            return formulario(model, clienteService.obtenerPorId(id), "Editar cliente", "/clientes/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("cliente") ClienteModel cliente,
                             BindingResult resultado, Model model, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return formulario(model, cliente, "Editar cliente", "/clientes/" + id);
        }
        try {
            clienteService.actualizar(id, cliente);
            flash.addFlashAttribute("mensaje", "Cliente actualizado correctamente");
            return "redirect:/clientes";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            return formulario(model, cliente, "Editar cliente", "/clientes/" + id);
        } catch (NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            clienteService.eliminar(id);
            flash.addFlashAttribute("mensaje", "Cliente eliminado correctamente");
        } catch (ReglaNegocioException | NoSuchElementException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }

    private String formulario(Model model, ClienteModel cliente, String titulo, String accion) {
        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", titulo);
        model.addAttribute("accion", accion);
        return "clientes/form";
    }
}
