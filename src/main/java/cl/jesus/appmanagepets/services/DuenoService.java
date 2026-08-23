package cl.jesus.appmanagepets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.jesus.appmanagepets.entities.Dueno;
import cl.jesus.appmanagepets.entities.Usuario;
import cl.jesus.appmanagepets.repositories.DuenoRepository;

@Service
public class DuenoService {

    private final DuenoRepository duenoRepository;
    private final UsuarioService usuarioService;

    public DuenoService(DuenoRepository duenoRepository, UsuarioService usuarioService) {
        this.duenoRepository = duenoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public Optional<Dueno> buscarPorUsername(String username) {
        return duenoRepository.findByUsuarioUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Dueno> listarTodos() {
        return duenoRepository.findAllConMascotas();
    }

    /**
     * Guarda la ficha del usuario autenticado. Sirve para crearla y para
     * editarla: si ya existe, se actualizan los campos sobre la misma fila en
     * vez de insertar una segunda ficha para la misma cuenta.
     */
    @Transactional
    public Dueno guardarFicha(String username, Dueno datos) {
        Usuario usuario = usuarioService.buscarPorUsername(username);
        Dueno ficha = duenoRepository.findByUsuarioUsername(username).orElseGet(Dueno::new);

        // El RUT es unico en la tabla: si es de otra ficha, avisamos en vez de
        // dejar que estalle la restriccion de la base de datos.
        if (!datos.getRut().equals(ficha.getRut()) && duenoRepository.existsByRut(datos.getRut())) {
            throw new IllegalArgumentException("Ese RUT ya esta registrado por otra cuenta");
        }

        ficha.setRut(datos.getRut());
        ficha.setNombre(datos.getNombre());
        ficha.setApellido(datos.getApellido());
        ficha.setEdad(datos.getEdad());
        ficha.setTelefono(datos.getTelefono());
        ficha.setDireccion(datos.getDireccion());
        ficha.setRegion(datos.getRegion());
        ficha.setComuna(datos.getComuna());
        ficha.setUsuario(usuario);

        return duenoRepository.save(ficha);
    }

    @Transactional(readOnly = true)
    public long contarDuenos() {
        return duenoRepository.count();
    }
}
