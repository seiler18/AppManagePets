package cl.jesus.appmanagepets.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cl.jesus.appmanagepets.services.DuenoService;
import cl.jesus.appmanagepets.services.MascotaService;

/**
 * Vista global. El acceso lo corta SecurityConfig con hasRole("ADMIN"),
 * asi que aqui no hace falta volver a comprobarlo.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;

    public AdminController(DuenoService duenoService, MascotaService mascotaService) {
        this.duenoService = duenoService;
        this.mascotaService = mascotaService;
    }

    @GetMapping
    public String panel(Model model) {
        model.addAttribute("duenos", duenoService.listarTodos());
        model.addAttribute("totalDuenos", duenoService.contarDuenos());
        model.addAttribute("totalMascotas", mascotaService.contarMascotas());
        return "admin/panel";
    }
}
