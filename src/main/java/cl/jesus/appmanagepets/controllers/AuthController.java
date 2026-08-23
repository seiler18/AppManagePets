package cl.jesus.appmanagepets.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cl.jesus.appmanagepets.services.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam String password2,
                            Model model,
                            RedirectAttributes flash) {

        if (username.isBlank() || username.length() < 4) {
            return volverConError(model, username, "El usuario debe tener al menos 4 caracteres");
        }
        if (password.length() < 6) {
            return volverConError(model, username, "La contrasena debe tener al menos 6 caracteres");
        }
        if (!password.equals(password2)) {
            return volverConError(model, username, "Las contrasenas no coinciden");
        }
        if (usuarioService.existeUsername(username)) {
            return volverConError(model, username, "Ese nombre de usuario ya esta tomado");
        }

        usuarioService.registrar(username, password);
        flash.addFlashAttribute("mensaje", "Cuenta creada. Ya puedes entrar.");
        return "redirect:/login";
    }

    /**
     * Devuelve el formulario con el error y el usuario ya escrito, en vez de
     * redirigir: asi no se pierde lo tecleado.
     */
    private String volverConError(Model model, String username, String error) {
        model.addAttribute("error", error);
        model.addAttribute("username", username);
        return "registro";
    }
}
