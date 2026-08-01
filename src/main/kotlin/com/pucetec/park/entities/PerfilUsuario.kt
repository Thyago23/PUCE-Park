package com.pucetec.park.entities

import jakarta.persistence.*

@Entity
@Table(name = "user_profiles")
class PerfilUsuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "username", length = 60, nullable = false, unique = true)
    val username: String = "",

    @Column(name = "full_name", length = 100)
    var nombreCompleto: String = "",

    @Column(name = "vehicle_plate", length = 15)
    var placaVehiculo: String = "",

    @Column(name = "permit_number", length = 20, unique = true, nullable = true)
    var numeroPermiso: String? = null,

    @Column(name = "dark_mode", nullable = false)
    var modoOscuro: Boolean = false
)
