package com.pucetec.park.entities

import jakarta.persistence.*

@Entity
@Table(name = "perfiles_usuario")
class PerfilUsuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "username", length = 60, nullable = false, unique = true)
    val username: String = "",

    @Column(name = "nombre_completo", length = 100)
    var nombreCompleto: String = "",

    @Column(name = "placa_vehiculo", length = 15)
    var placaVehiculo: String = "",

    @Column(name = "modo_oscuro", nullable = false)
    var modoOscuro: Boolean = false
)
