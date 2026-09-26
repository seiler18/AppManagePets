package cl.jesus.appmanagepets.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mascota")
@Getter
@Setter
@NoArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cada @Size replica el length de su @Column. Sin el, un texto mas largo
    // pasa la validacion y revienta en el INSERT de Postgres con un 500 en vez
    // de volver al formulario con el aviso.
    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(max = 60, message = "Maximo 60 caracteres")
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 40, message = "Edad no valida")
    @Column(nullable = false)
    private Integer edad;

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30, message = "Maximo 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipo;

    @Size(max = 60, message = "Maximo 60 caracteres")
    @Column(length = 60)
    private String raza;

    @Size(max = 10, message = "Maximo 10 caracteres")
    @Column(length = 10)
    private String sexo;

    @Size(max = 300, message = "Maximo 300 caracteres")
    @Column(length = 300)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dueno_id", nullable = false)
    private Dueno dueno;
}
