package cl.jesus.appmanagepets.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 40, message = "Edad no valida")
    @Column(nullable = false)
    private Integer edad;

    @NotBlank(message = "El tipo es obligatorio")
    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(length = 60)
    private String raza;

    @Column(length = 10)
    private String sexo;

    @Column(length = 300)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dueno_id", nullable = false)
    private Dueno dueno;
}
