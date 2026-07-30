package com.pucetec.park.entities

import jakarta.persistence.*

@Entity
@Table(
    name = "zonas_parqueo",
    uniqueConstraints = [UniqueConstraint(columnNames = ["nombre"])]
)
class ZonaParqueo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "nombre", length = 50, nullable = false, unique = true)
    var nombre: String = "",

    @Column(name = "descripcion", length = 255)
    var descripcion: String = "",

    @Column(name = "capacidad_maxima", nullable = false)
    var capacidadMaxima: Int = 0,

    @Column(name = "ubicacion", length = 255)
    var ubicacion: String = "",

    @Column(name = "latitud")
    var latitud: Double? = null,

    @Column(name = "longitud")
    var longitud: Double? = null,
)
