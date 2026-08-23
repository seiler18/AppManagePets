package cl.jesus.appmanagepets.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import cl.jesus.appmanagepets.services.DuenoService;
import cl.jesus.appmanagepets.services.MascotaService;

@Controller
public class HomeController {

    private final DuenoService duenoService;
    private final MascotaService mascotaService;

    public HomeController(DuenoService duenoService, MascotaService mascotaService) {
        this.duenoService = duenoService;
        this.mascotaService = mascotaService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalDuenos", duenoService.contarDuenos());
        model.addAttribute("totalMascotas", mascotaService.contarMascotas());
        return "index";
    }

    /**
     * RequestMapping sin method a proposito: accessDeniedPage hace un FORWARD
     * conservando el metodo original, asi que un POST rechazado (borrar una
     * mascota ajena, o un token CSRF invalido) llega aqui como POST. Con
     * @GetMapping eso respondia 405 Method Not Allowed en vez de mostrar el
     * aviso con su 403.
     */
    @RequestMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
