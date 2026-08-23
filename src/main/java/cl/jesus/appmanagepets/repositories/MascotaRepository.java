package cl.jesus.appmanagepets.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.jesus.appmanagepets.entities.Mascota;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByDuenoIdOrderByNombre(Long duenoId);

    /**
     * Busca por id PERO exigiendo que la mascota sea del usuario indicado.
     * Se usa en editar y eliminar: si el id no es suyo, devuelve vacio y el
     * controlador responde 403. Sin este filtro, cambiar el id en la URL
     * dejaria a cualquiera tocar mascotas ajenas.
     */
    Optional<Mascota> findByIdAndDuenoUsuarioUsername(Long id, String username);

    @Query("SELECT m FROM Mascota m JOIN FETCH m.dueno ORDER BY m.nombre")
    List<Mascota> findAllConDueno();

    long countByDuenoId(Long duenoId);
}
