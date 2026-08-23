package cl.jesus.appmanagepets.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cl.jesus.appmanagepets.entities.Dueno;
import cl.jesus.appmanagepets.services.DuenoService;
import jakarta.validation.Valid;

/**
 * Ficha de datos personales del usuario autenticado. Es el paso previo a
 * registrar mascotas: una mascota siempre cuelga de un dueno.
 */
@Controller
public class PerfilController {

    private final DuenoService duenoService;

    public PerfilController(DuenoService duenoService) {
        this.duenoService = duenoService;
    }

    @GetMapping("/perfil")
    public String mostrar(Principal principal, Model model) {
        Dueno ficha = duenoService.buscarPorUsername(principal.getName()).orElseGet(Dueno::new);
        model.addAttribute("dueno", ficha);
        model.addAttribute("esNueva", ficha.getId() == null);
        return "perfil";
    }

    @PostMapping("/perfil")
    public String guardar(@Valid @ModelAttribute("dueno") Dueno dueno,
                          BindingResult errores,
                          Principal principal,
                          Model model,
                          RedirectAttributes flash) {

        if (errores.hasErrors()) {
            model.addAttribute("esNueva", dueno.getId() == null);
            return "perfil";
        }

        try {
            duenoService.guardarFicha(principal.getName(), dueno);
        } catch (IllegalArgumentException e) {
            errores.rejectValue("rut", "rut.duplicado", e.getMessage());
            model.addAttribute("esNueva", dueno.getId() == null);
            return "perfil";
        }

        flash.addFlashAttribute("mensaje", "Tus datos quedaron guardados");
        return "redirect:/mascotas";
    }
}
