package cl.jesus.appmanagepets.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos personales de quien registra mascotas. Uno por cuenta.
 */
@Entity
@Table(name = "dueno")
@Getter
@Setter
@NoArgsConstructor
public class Dueno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9kK]$", message = "Formato de RUT: 12345678-9")
    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    // Cada @Size replica el length de su @Column: sin el, un texto mas largo
    // pasa la validacion y revienta en el INSERT de Postgres con un 500.
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "Maximo 60 caracteres")
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "Maximo 60 caracteres")
    @Column(nullable = false, length = 60)
    private String apellido;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "Debe ser mayor de edad")
    @Max(value = 120, message = "Edad no valida")
    @Column(nullable = false)
    private Integer edad;

    @Size(max = 20, message = "Maximo 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Size(max = 200, message = "Maximo 200 caracteres")
    @Column(length = 200)
    private String direccion;

    @Size(max = 60, message = "Maximo 60 caracteres")
    @Column(length = 60)
    private String region;

    @Size(max = 60, message = "Maximo 60 caracteres")
    @Column(length = 60)
    private String comuna;

    /**
     * Dueno pertenece a una cuenta. Es la clave de todo el control de acceso:
     * un USER solo puede ver las mascotas cuyo dueno tiene su mismo usuario.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mascota> mascotas = new ArrayList<>();

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
