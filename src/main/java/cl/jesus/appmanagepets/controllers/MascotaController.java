package cl.jesus.appmanagepets.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cl.jesus.appmanagepets.entities.Dueno;
import cl.jesus.appmanagepets.entities.Mascota;
import cl.jesus.appmanagepets.services.DuenoService;
import cl.jesus.appmanagepets.services.MascotaService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final DuenoService duenoService;

    public MascotaController(MascotaService mascotaService, DuenoService duenoService) {
        this.mascotaService = mascotaService;
        this.duenoService = duenoService;
    }

    @GetMapping
    public String listar(Principal principal, Model model) {
        // Sin ficha no hay a quien colgarle una mascota: se manda a crearla.
        if (duenoService.buscarPorUsername(principal.getName()).isEmpty()) {
            return "redirect:/perfil";
        }
        Dueno ficha = duenoService.buscarPorUsername(principal.getName()).orElseThrow();
        model.addAttribute("dueno", ficha);
        model.addAttribute("mascotas", mascotaService.listarDe(principal.getName()));
        return "mascotas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Principal principal, Model model) {
        if (duenoService.buscarPorUsername(principal.getName()).isEmpty()) {
            return "redirect:/perfil";
        }
        model.addAttribute("mascota", new Mascota());
        return "mascotas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Principal principal, Model model) {
        // buscarPropia lanza AccessDeniedException si el id no es del usuario.
        model.addAttribute("mascota", mascotaService.buscarPropia(id, principal.getName()));
        return "mascotas/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("mascota") Mascota mascota,
                          BindingResult errores,
                          Principal principal,
                          RedirectAttributes flash) {

        if (errores.hasErrors()) {
            return "mascotas/form";
        }

        boolean esNueva = mascota.getId() == null;
        mascotaService.guardar(mascota, principal.getName());
        flash.addFlashAttribute("mensaje",
                esNueva ? "Mascota registrada: " + mascota.getNombre()
                        : "Mascota actualizada: " + mascota.getNombre());
        return "redirect:/mascotas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        mascotaService.eliminar(id, principal.getName());
        flash.addFlashAttribute("mensaje", "Mascota eliminada");
        return "redirect:/mascotas";
    }
}
