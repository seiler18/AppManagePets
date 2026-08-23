package cl.jesus.appmanagepets.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.jesus.appmanagepets.entities.Dueno;

public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    Optional<Dueno> findByUsuarioUsername(String username);

    boolean existsByRut(String rut);

    /**
     * Para el panel de admin: trae duenos con sus mascotas y su cuenta en una
     * sola consulta. Dos motivos para el JOIN FETCH:
     *  - sin el de mascotas, listar N duenos dispara N consultas mas (N+1);
     *  - sin el de usuario, la plantilla revienta con LazyInitializationException,
     *    porque open-in-view=false cierra la sesion antes de renderizar.
     */
    @Query("SELECT DISTINCT d FROM Dueno d LEFT JOIN FETCH d.mascotas JOIN FETCH d.usuario ORDER BY d.apellido, d.nombre")
    List<Dueno> findAllConMascotas();
}
