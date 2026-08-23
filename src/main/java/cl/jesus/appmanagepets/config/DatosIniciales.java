package cl.jesus.appmanagepets.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cl.jesus.appmanagepets.entities.Dueno;
import cl.jesus.appmanagepets.entities.Mascota;
import cl.jesus.appmanagepets.entities.Usuario;
import cl.jesus.appmanagepets.repositories.DuenoRepository;
import cl.jesus.appmanagepets.repositories.MascotaRepository;
import cl.jesus.appmanagepets.repositories.UsuarioRepository;

/**
 * Siembra la cuenta de administrador y unos registros de muestra.
 *
 * Es idempotente a proposito: con Postgres los datos sobreviven al redeploy,
 * asi que esto corre en cada arranque y debe no hacer nada si ya hay datos.
 * Un data.sql con INSERT fijos duplicaria filas en cada reinicio.
 */
@Component
public class DatosIniciales implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final UsuarioRepository usuarioRepository;
    private final DuenoRepository duenoRepository;
    private final MascotaRepository mascotaRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * En produccion se pasa por variable de entorno APP_ADMIN_PASSWORD.
     * El valor por defecto solo sirve para desarrollo local.
     */
    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.datos-demo:true}")
    private boolean cargarDemo;

    public DatosIniciales(UsuarioRepository usuarioRepository, DuenoRepository duenoRepository,
                          MascotaRepository mascotaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.duenoRepository = duenoRepository;
        this.mascotaRepository = mascotaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        crearAdmin();
        if (cargarDemo) {
            crearDatosDemo();
        }
    }

    private void crearAdmin() {
        if (usuarioRepository.existsByUsername("admin")) {
            log.info("La cuenta admin ya existe, no se toca");
            return;
        }
        usuarioRepository.save(new Usuario("admin", passwordEncoder.encode(adminPassword), List.of("ADMIN", "USER")));
        log.info("Cuenta admin creada");
    }

    /**
     * Solo si no hay ninguna ficha todavia: una base recien creada arranca con
     * algo que mirar, y una que ya tiene registros reales no se contamina.
     */
    private void crearDatosDemo() {
        if (duenoRepository.count() > 0) {
            log.info("Ya hay duenos registrados, se omiten los datos de demo");
            return;
        }

        Usuario cuenta = usuarioRepository.findByUsername("demo")
                .orElseGet(() -> usuarioRepository.save(
                        new Usuario("demo", passwordEncoder.encode("demo123"), List.of("USER"))));

        Dueno dueno = new Dueno();
        dueno.setRut("11111111-1");
        dueno.setNombre("Ana");
        dueno.setApellido("Perez");
        dueno.setEdad(34);
        dueno.setTelefono("+56 9 1234 5678");
        dueno.setDireccion("Av. Siempre Viva 742");
        dueno.setRegion("Metropolitana");
        dueno.setComuna("Providencia");
        dueno.setUsuario(cuenta);
        duenoRepository.save(dueno);

        mascotaRepository.saveAll(List.of(
                mascota("Kiro", 4, "Perro", "Border Collie", "Macho", "Muy activo, necesita paseos largos", dueno),
                mascota("Luna", 2, "Gato", "Siames", "Hembra", "Duerme casi todo el dia", dueno)));

        log.info("Datos de demo cargados: dueno Ana Perez con 2 mascotas");
    }

    private Mascota mascota(String nombre, int edad, String tipo, String raza,
                            String sexo, String descripcion, Dueno dueno) {
        Mascota m = new Mascota();
        m.setNombre(nombre);
        m.setEdad(edad);
        m.setTipo(tipo);
        m.setRaza(raza);
        m.setSexo(sexo);
        m.setDescripcion(descripcion);
        m.setDueno(dueno);
        return m;
    }
}
