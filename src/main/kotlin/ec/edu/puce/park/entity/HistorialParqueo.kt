package ec.edu.puce.park.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "historial_parqueo")
data class HistorialParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puesto_id", nullable = false)
    var puesto: PuestoParqueo,

    @Column(name = "username", length = 60, nullable = false)
    var username: String,

    @Column(name = "fecha_ingreso", nullable = false)
    var fechaIngreso: LocalDateTime = LocalDateTime.now(),

    @Column(name = "fecha_salida")
    var fechaSalida: LocalDateTime? = null
)
