package cl.jesus.appmanagepets.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cuenta de acceso. Se llama app_user porque "user" es palabra reservada en
 * Postgres: sin el @Table el CREATE TABLE falla en produccion.
 */
@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    /** Siempre hash BCrypt, nunca texto plano. 60 caracteres justos. */
    @Column(nullable = false, length = 60)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "rol")
    private List<String> roles = new ArrayList<>();

    /** Ficha de datos personales. Null hasta que el usuario la completa. */
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Dueno dueno;

    public Usuario(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>(roles);
    }

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String rol : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
        }
        return authorities;
    }

    public boolean esAdmin() {
        return roles.contains("ADMIN");
    }
}
