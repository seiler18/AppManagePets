package cl.jesus.appmanagepets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import cl.jesus.appmanagepets.entities.Dueno;
import cl.jesus.appmanagepets.entities.Mascota;
import cl.jesus.appmanagepets.services.DuenoService;
import cl.jesus.appmanagepets.services.MascotaService;
import cl.jesus.appmanagepets.services.UsuarioService;

/**
 * La regla de negocio que mas duele si se rompe: cada usuario ve y edita
 * SOLO sus mascotas. Se prueba en el servicio, no en la plantilla, porque es
 * ahi donde vive la comprobacion.
 */
@SpringBootTest
class PropiedadDeMascotasTest {

    @Autowired private UsuarioService usuarioService;
    @Autowired private DuenoService duenoService;
    @Autowired private MascotaService mascotaService;

    private Long idMascotaDeAna;

    @BeforeEach
    void prepararDosUsuariosConMascota() {
        if (!usuarioService.existeUsername("ana")) {
            usuarioService.registrar("ana", "clave123");
            duenoService.guardarFicha("ana", ficha("22222222-2", "Ana", "Soto", 30));
            Mascota m = new Mascota();
            m.setNombre("Kiro");
            m.setEdad(3);
            m.setTipo("Perro");
            mascotaService.guardar(m, "ana");
        }
        if (!usuarioService.existeUsername("beto")) {
            usuarioService.registrar("beto", "clave123");
            duenoService.guardarFicha("beto", ficha("33333333-3", "Beto", "Rojas", 41));
        }

        // Se resuelve SIEMPRE, no solo al crear: JUnit instancia la clase de
        // nuevo en cada test, pero el contexto de Spring (y sus datos) se
        // reutiliza. Guardar el id solo dentro del if lo dejaria en null a
        // partir del segundo test.
        idMascotaDeAna = mascotaService.listarDe("ana").get(0).getId();
    }

    @Test
    void anaVeSuMascota() {
        assertThat(mascotaService.buscarPropia(idMascotaDeAna, "ana").getNombre()).isEqualTo("Kiro");
    }

    @Test
    void betoNoPuedeVerLaMascotaDeAna() {
        assertThatThrownBy(() -> mascotaService.buscarPropia(idMascotaDeAna, "beto"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void betoNoPuedeBorrarLaMascotaDeAna() {
        assertThatThrownBy(() -> mascotaService.eliminar(idMascotaDeAna, "beto"))
                .isInstanceOf(AccessDeniedException.class);
        assertThat(mascotaService.buscarPropia(idMascotaDeAna, "ana")).isNotNull();
    }

    @Test
    void cadaUnoListaSoloLoSuyo() {
        assertThat(mascotaService.listarDe("ana")).extracting(Mascota::getNombre).containsExactly("Kiro");
        assertThat(mascotaService.listarDe("beto")).isEmpty();
    }

    private Dueno ficha(String rut, String nombre, String apellido, int edad) {
        Dueno d = new Dueno();
        d.setRut(rut);
        d.setNombre(nombre);
        d.setApellido(apellido);
        d.setEdad(edad);
        return d;
    }
}
