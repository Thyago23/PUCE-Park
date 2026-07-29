package ec.edu.puce.park.entity

import jakarta.persistence.*

@Entity
@Table(name = "zonas_parqueo")
data class ZonaParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "nombre", length = 50, nullable = false)
    var nombre: String,

    @Column(name = "descripcion", length = 255)
    var descripcion: String? = null
)
