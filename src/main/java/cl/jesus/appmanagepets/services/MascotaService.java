package cl.jesus.appmanagepets.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.jesus.appmanagepets.entities.Dueno;
import cl.jesus.appmanagepets.entities.Mascota;
import cl.jesus.appmanagepets.repositories.MascotaRepository;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final DuenoService duenoService;

    public MascotaService(MascotaRepository mascotaRepository, DuenoService duenoService) {
        this.mascotaRepository = mascotaRepository;
        this.duenoService = duenoService;
    }

    @Transactional(readOnly = true)
    public List<Mascota> listarDe(String username) {
        return duenoService.buscarPorUsername(username)
                .map(d -> mascotaRepository.findByDuenoIdOrderByNombre(d.getId()))
                .orElseGet(List::of);
    }

    @Transactional(readOnly = true)
    public List<Mascota> listarTodas() {
        return mascotaRepository.findAllConDueno();
    }

    /**
     * Recupera una mascota comprobando que sea del usuario que la pide.
     * Cualquier otro id -> AccessDeniedException, no "no encontrada": asi el
     * usuario no puede deducir que ids existen probando la URL.
     */
    @Transactional(readOnly = true)
    public Mascota buscarPropia(Long id, String username) {
        return mascotaRepository.findByIdAndDuenoUsuarioUsername(id, username)
                .orElseThrow(() -> new AccessDeniedException("La mascota " + id + " no pertenece a " + username));
    }

    @Transactional
    public Mascota guardar(Mascota datos, String username) {
        Dueno dueno = duenoService.buscarPorUsername(username)
                .orElseThrow(() -> new IllegalStateException("Completa tu ficha antes de registrar mascotas"));

        // Si viene con id, es una edicion: se valida la propiedad antes de
        // tocar nada. Si no, es una mascota nueva para este dueno.
        Mascota mascota = (datos.getId() != null) ? buscarPropia(datos.getId(), username) : new Mascota();

        mascota.setNombre(datos.getNombre());
        mascota.setEdad(datos.getEdad());
        mascota.setTipo(datos.getTipo());
        mascota.setRaza(datos.getRaza());
        mascota.setSexo(datos.getSexo());
        mascota.setDescripcion(datos.getDescripcion());
        mascota.setDueno(dueno);

        return mascotaRepository.save(mascota);
    }

    @Transactional
    public void eliminar(Long id, String username) {
        mascotaRepository.delete(buscarPropia(id, username));
    }

    @Transactional(readOnly = true)
    public long contarMascotas() {
        return mascotaRepository.count();
    }
}
