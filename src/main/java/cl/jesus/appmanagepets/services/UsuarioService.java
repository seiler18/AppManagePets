package cl.jesus.appmanagepets.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.jesus.appmanagepets.entities.Usuario;
import cl.jesus.appmanagepets.repositories.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(usuario.getAuthorities())
                .build();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado sin registro: " + username));
    }

    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    /** Registra una cuenta nueva con rol USER y la contrasena hasheada. */
    @Transactional
    public Usuario registrar(String username, String passwordEnClaro) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ese nombre de usuario ya esta tomado");
        }
        Usuario usuario = new Usuario(username, passwordEncoder.encode(passwordEnClaro), List.of("USER"));
        return usuarioRepository.save(usuario);
    }
}
