package ec.edu.puce.park.entity

import jakarta.persistence.*

@Entity
@Table(name = "perfiles_usuario")
data class PerfilUsuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "username", length = 60, nullable = false, unique = true)
    var username: String,

    @Column(name = "nombre_completo", length = 100)
    var nombreCompleto: String? = null,

    @Column(name = "placa_vehiculo", length = 15)
    var placaVehiculo: String? = null,

    @Column(name = "modo_oscuro", nullable = false)
    var modoOscuro: Boolean = false
)
