package ec.edu.puce.park.entity

import jakarta.persistence.*

@Entity
@Table(name = "puestos_parqueo")
data class PuestoParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id", nullable = false)
    var zona: ZonaParqueo,

    @Column(name = "numero_puesto", length = 10, nullable = false)
    var numeroPuesto: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20, nullable = false)
    var estado: EstadoPuesto = EstadoPuesto.DISPONIBLE
)

enum class EstadoPuesto {
    DISPONIBLE, OCUPADO
}
