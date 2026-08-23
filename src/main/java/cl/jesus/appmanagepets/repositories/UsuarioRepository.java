package cl.jesus.appmanagepets.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.jesus.appmanagepets.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}
